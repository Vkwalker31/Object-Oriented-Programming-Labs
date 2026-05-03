package com.example.bookexchange.api;

import com.example.bookexchange.api.dto.ExchangeCreateRequest;
import com.example.bookexchange.api.dto.ExchangeResponse;
import com.example.bookexchange.api.dto.ExchangeStatusUpdateRequest;
import com.example.bookexchange.api.dto.BookMovementResponse;
import com.example.bookexchange.controllers.ExchangeUseCase;
import com.example.bookexchange.models.BookMovement;
import com.example.bookexchange.models.ExchangeRequest;
import com.example.bookexchange.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/exchanges")
@RequiredArgsConstructor
@Tag(name = "Exchanges")
public class ExchangeApiController {
    private final ExchangeUseCase exchangeUseCase;

    @PostMapping
    @Operation(summary = "Create exchange request for inventory item")
    public ResponseEntity<ApiResponse<ExchangeResponse>> request(Authentication authentication,
                                                                 @Valid @RequestBody ExchangeCreateRequest request) {
        UUID requesterId = (UUID) authentication.getPrincipal();
        ExchangeRequest created = exchangeUseCase.requestExchange(requesterId, request.inventoryId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toResponse(created)));
    }

    @PatchMapping("/{exchangeId}/status")
    @Operation(summary = "Update exchange status")
    public ResponseEntity<ApiResponse<ExchangeResponse>> update(@PathVariable UUID exchangeId,
                                                                @Valid @RequestBody ExchangeStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(exchangeUseCase.updateStatus(exchangeId, request.status()))));
    }

    @GetMapping("/incoming")
    @Operation(summary = "Incoming requests for owner")
    public ResponseEntity<ApiResponse<List<ExchangeResponse>>> incoming(Authentication authentication) {
        UUID ownerId = (UUID) authentication.getPrincipal();
        List<ExchangeResponse> response = exchangeUseCase.getIncoming(ownerId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/outgoing")
    @Operation(summary = "Outgoing requests for requester")
    public ResponseEntity<ApiResponse<List<ExchangeResponse>>> outgoing(Authentication authentication) {
        UUID requesterId = (UUID) authentication.getPrincipal();
        List<ExchangeResponse> response = exchangeUseCase.getOutgoing(requesterId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/history")
    @Operation(summary = "Get exchange movement history for current user")
    public ResponseEntity<ApiResponse<List<BookMovementResponse>>> history(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<BookMovementResponse> response = exchangeUseCase.getHistory(userId).stream().map(this::toMovementResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private ExchangeResponse toResponse(ExchangeRequest request) {
        return new ExchangeResponse(
                request.getId(),
                request.getRequesterId(),
                request.getOwnerId(),
                request.getInventoryId(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private BookMovementResponse toMovementResponse(BookMovement movement) {
        return new BookMovementResponse(
                movement.getId(),
                movement.getExchangeId(),
                movement.getInventoryId(),
                movement.getFromUserId(),
                movement.getToUserId(),
                movement.getStatus(),
                movement.getCreatedAt()
        );
    }
}
