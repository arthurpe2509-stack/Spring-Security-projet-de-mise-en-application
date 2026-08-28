package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.dto.LoginRequest;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepositoryInjected;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService tokenService;

    public AuthController(
            UserRepository userRepositoryInjected, RoleRepository roleRepositoryInjected,
            AuthenticationManager authManagerInjected,
            PasswordEncoder passwordEncoderInjected,
            JwtService tokenService) {
        this.userRepository = userRepositoryInjected;
        this.roleRepositoryInjected = roleRepositoryInjected;
        this.authManager = authManagerInjected;
        this.passwordEncoder = passwordEncoderInjected;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public LoginRequest login(@RequestBody User user) {

        Authentication auth = this.authManager.authenticate(new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword()));
        String token = tokenService.generateToken(auth);

        // récupère l'utilisateur connecté
        User userConnected = (User) auth.getPrincipal();
        return new LoginRequest(token, userConnected.getUsername());
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}