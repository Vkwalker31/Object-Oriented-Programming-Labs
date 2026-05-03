package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.ExchangeStatus;

import java.time.Instant;
import java.util.UUID;

public record ExchangeResponse(
        UUID id,
        UUID requesterId,
        UUID ownerId,
        UUID inventoryId,
        ExchangeStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
