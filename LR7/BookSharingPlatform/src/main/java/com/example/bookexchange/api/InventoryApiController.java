package com.example.bookexchange.api;

import com.example.bookexchange.api.dto.InventoryCreateRequest;
import com.example.bookexchange.api.dto.InventoryResponse;
import com.example.bookexchange.controllers.InventoryUseCase;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory")
public class InventoryApiController {
    private final InventoryUseCase inventoryUseCase;

    @PostMapping
    @Operation(summary = "Add book to personal inventory")
    public ResponseEntity<ApiResponse<InventoryResponse>> add(Authentication authentication,
                                                              @Valid @RequestBody InventoryCreateRequest request) {
        UUID userId = (UUID) authentication.getPrincipal();
        InventoryItem item = inventoryUseCase.addToInventory(userId, request.bookId(), request.condition());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toResponse(item)));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user inventory")
    public ResponseEntity<ApiResponse<List<InventoryResponse>>> myInventory(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<InventoryResponse> response = inventoryUseCase.getUserInventory(userId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PatchMapping("/{inventoryId}/status")
    @Operation(summary = "Update inventory status")
    public ResponseEntity<ApiResponse<InventoryResponse>> updateStatus(@PathVariable UUID inventoryId,
                                                                       @RequestParam InventoryStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(toResponse(inventoryUseCase.updateStatus(inventoryId, status))));
    }

    private InventoryResponse toResponse(InventoryItem item) {
        return new InventoryResponse(item.getId(), item.getUserId(), item.getBookId(), item.getStatus(), item.getCondition());
    }
}
