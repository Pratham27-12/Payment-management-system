package org.example.controller;

import org.example.model.dto.User;
import org.example.model.request.LoginRequest;
import org.example.model.response.AuthResponse;
import org.example.repository.UserRepository;
import org.example.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            Optional<User> userOpt = userRepository.getUserByUserName(request.getUsername());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

                return ResponseEntity.ok(AuthResponse.builder()
                        .token(token)
                        .username(user.getUsername())
                        .role(user.getRole().name())
                        .message("Login successful")
                        .build());
            }

            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .message("User not found")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(AuthResponse.builder()
                    .message("Invalid credentials")
                    .build());
        }
    }
}

