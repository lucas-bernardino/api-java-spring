package com.example.demo.services.auth;

import com.example.demo.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (Objects.isNull(authHeader)) { return null; }

        return authHeader.replace("Bearer", "");

    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        var token = recoverToken(request);
        // se for null ele segue para o proximo filtro (UsernamePasswordAuthenticationFilter.class)

        if (!Objects.isNull(token)) {
            var username = tokenService.verifyToken(token);
            UserDetails user = userRepository.findByUsername(username);

            var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);

        // O usuario foi salvo no contexto da aplicação para ir pro proximo filter no SecurityConfig.
        // Caso nao tenha token, nada sera salvo e entao quando chegar no hasRole ou no autenticado, nada sera encontrado e assim nao permitira entrar no sistema.


    }
}
