package com.example.demo.exceptions;

import com.example.demo.services.exceptions.DataBindingViolationException;
import com.example.demo.services.exceptions.ObjectNotFoundException;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL_EXCEPTION_HANDLER") // para exibir no console
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{

    // caso esteja em ambiente de desenvolvimento, printa o trace na response. Caso contrario, nao.
    // Essa variavel 'server.error.include-exception' está no application.properties
    @Value("${server.error.include-exception}")
    private boolean printStackTrace;
    private ResponseEntity<Object> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
        if (printStackTrace) {
            errorResponse.setStatckTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private ResponseEntity<Object> buildErrorResponse (Exception exception, HttpStatus httpStatus, WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    // Esse método é responsável por lidar com erros do tipo UNPROCESSABLE_ENTITY (422), que é lançado quando são enviados dados inválidos e não foi possível processa-los.
    // Para fazer isso, é utilizado a classe ErrorResponse e para cada campo com mensagem adiciona-se na lista de erros que serão retornados no body.
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Erro de validacao. O campo 'errors' possui os detalhes");

        for (FieldError fieldError: methodArgumentNotValidException.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    // Esse método serve para tratar os erros inesperados, onde não é sabido o que o causou.
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleAllUncaughtException(Exception exception, WebRequest request) {
        final String errorMessage = "Ocorreu um erro inesperado";
        log.error(errorMessage, exception);
        return buildErrorResponse(exception, errorMessage, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    // Esse método é responsável por lidar com exceções em que são enviados dados que já constam no banco de dados,
    // como por exemplo enviar um usuario com o nome de outro usuário já cadastrado.
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException dataIntegrityViolationException, WebRequest request) {
        String errorMessage = dataIntegrityViolationException.getMostSpecificCause().getMessage();
        log.error("Falha ao salvar dados com problemas de integridades: " + errorMessage + dataIntegrityViolationException);
        return buildErrorResponse(dataIntegrityViolationException, errorMessage, HttpStatus.CONFLICT, request);
    }



    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException constraintViolationException, WebRequest request) {
        log.error("Falha ao validar o elemento. " + constraintViolationException);
        return buildErrorResponse(constraintViolationException, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }


    // Esse método trabalha em conjunto com a classe herdado na pasta /services/exceptions, de modo que quando se busca um elemento
    // por id que ainda não foi cadastrado é lançado essa exceção.
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException objectNotFoundException, WebRequest request) {
        log.error("Falha ao encontrar o elemento requisitado. " + objectNotFoundException);

        return buildErrorResponse(objectNotFoundException, HttpStatus.NOT_FOUND, request);
    }


    // Esse método é responsável por tratar as exceções lançadas quando se tenta remover um elemento que possui dependencia de outro
    @ExceptionHandler(DataBindingViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleDataBindingViolationException(DataBindingViolationException dataBindingViolationException, WebRequest request) {
        log.error("Falha ao salvar dado com elementos associados " + dataBindingViolationException);

        return buildErrorResponse(dataBindingViolationException, HttpStatus.CONFLICT, request);
    }


    @ExceptionHandler(InternalAuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException internalAuthenticationServiceException, WebRequest webRequest) {
        String errorMessage = "Falha no login: Usuario nao cadastrado";
        log.error(errorMessage + internalAuthenticationServiceException);

        return buildErrorResponse(internalAuthenticationServiceException, errorMessage, HttpStatus.NOT_FOUND, webRequest);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException badCredentialsException, WebRequest webRequest) {
        String errorMessage = "Falha no login: Senha incorreta";
        log.error(errorMessage + badCredentialsException);

        return buildErrorResponse(badCredentialsException, errorMessage, HttpStatus.UNAUTHORIZED, webRequest);
    }



}
