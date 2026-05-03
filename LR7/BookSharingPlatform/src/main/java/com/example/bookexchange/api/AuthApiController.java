package com.example.bookexchange.api;

import com.example.bookexchange.api.dto.AuthLoginRequest;
import com.example.bookexchange.api.dto.AuthRegisterRequest;
import com.example.bookexchange.api.dto.AuthResponse;
import com.example.bookexchange.api.dto.UserResponse;
import com.example.bookexchange.controllers.AuthUseCase;
import com.example.bookexchange.models.AuthToken;
import com.example.bookexchange.models.User;
import com.example.bookexchange.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthApiController {
    private final AuthUseCase authUseCase;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        User user = authUseCase.register(request.username(), request.email(), request.password());
        UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive JWT")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthToken token = authUseCase.login(request.email(), request.password());
        return ResponseEntity.ok(ApiResponse.ok(new AuthResponse(token.getAccessToken(), token.getTokenType())));
    }
}
