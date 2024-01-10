package com.example.demo.controllers;

import com.example.demo.models.LoginRecord;
import com.example.demo.models.RegisterRecord;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.auth.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Validated LoginRecord login) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(login.username(), login.password());
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        // Aqui, o spring ja cuida da parte da criptografia, que foi definida no SecurityConfig.
        // Ele faz isso por tras dos panos

        var token = tokenService.generateToken((User) authenticate.getPrincipal());

        return ResponseEntity.ok(token);
    }



    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Validated(User.CreateUser.class) RegisterRecord register) {
        if (userRepository.findByUsername(register.username()) != null) {
            return ResponseEntity.badRequest().build();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(register.password());

        User newUserRegister = new User(register.username(), encryptedPassword, register.role());

        userRepository.save(newUserRegister);

        return ResponseEntity.ok().build();

    }

}
