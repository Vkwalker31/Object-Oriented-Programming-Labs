package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.ExchangeStatus;

import java.time.Instant;
import java.util.UUID;

public record BookMovementResponse(
        UUID id,
        UUID exchangeId,
        UUID inventoryId,
        UUID fromUserId,
        UUID toUserId,
        ExchangeStatus status,
        Instant createdAt
) {
}
