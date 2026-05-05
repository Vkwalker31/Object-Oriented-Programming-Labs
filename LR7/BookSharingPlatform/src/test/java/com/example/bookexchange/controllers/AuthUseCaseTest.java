package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.UserGateway;
import com.example.bookexchange.models.AuthToken;
import com.example.bookexchange.models.User;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.UnauthorizedException;
import com.example.bookexchange.shared.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserGateway userGateway;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    void register_success() {
        when(userGateway.existsByEmail("john@example.com")).thenReturn(false);
        when(userGateway.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");
        when(userGateway.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = authUseCase.register("john", "john@example.com", "secret123");

        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("hashed", result.getPasswordHash());
        assertEquals(BigDecimal.ZERO, result.getRating());
        assertNotNull(result.getId());
    }

    @Test
    void register_emailExists_throwsConflict() {
        when(userGateway.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> authUseCase.register("john", "john@example.com", "secret123"));
    }

    @Test
    void login_invalidPassword_throwsUnauthorized() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .email("john@example.com")
                .passwordHash("hashed")
                .rating(BigDecimal.ZERO)
                .build();
        when(userGateway.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authUseCase.login("john@example.com", "wrong"));
    }

    @Test
    void login_success() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .email("john@example.com")
                .passwordHash("hashed")
                .rating(BigDecimal.ZERO)
                .build();
        when(userGateway.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(eq(user.getId()), eq("john"), eq("john@example.com"))).thenReturn("jwt-token");

        AuthToken token = authUseCase.login("john@example.com", "secret123");

        assertEquals("jwt-token", token.getAccessToken());
        assertEquals("Bearer", token.getTokenType());
    }
}
