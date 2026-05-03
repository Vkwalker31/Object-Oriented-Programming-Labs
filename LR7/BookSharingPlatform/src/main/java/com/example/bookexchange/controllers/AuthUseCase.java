package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.UserGateway;
import com.example.bookexchange.models.AuthToken;
import com.example.bookexchange.models.User;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.UnauthorizedException;
import com.example.bookexchange.shared.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUseCase {
    private final UserGateway userGateway;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public User register(String username, String email, String rawPassword) {
        if (userGateway.existsByEmail(email)) {
            throw new ConflictException("Email is already registered");
        }
        if (userGateway.existsByUsername(username)) {
            throw new ConflictException("Username is already taken");
        }
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .rating(BigDecimal.ZERO)
                .build();
        User saved = userGateway.save(user);
        log.info("Registered new user: {}", saved.getId());
        return saved;
    }

    public AuthToken login(String email, String rawPassword) {
        User user = userGateway.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getEmail());
        log.info("User logged in: {}", user.getId());
        return AuthToken.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }
}
