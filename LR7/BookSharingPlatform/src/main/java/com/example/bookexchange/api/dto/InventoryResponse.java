package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.InventoryStatus;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        UUID userId,
        UUID bookId,
        InventoryStatus status,
        String condition
) {
}
