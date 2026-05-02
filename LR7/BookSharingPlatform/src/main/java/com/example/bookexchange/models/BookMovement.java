package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder
public class BookMovement {
    UUID id;
    UUID exchangeId;
    UUID inventoryId;
    UUID fromUserId;
    UUID toUserId;
    ExchangeStatus status;
    Instant createdAt;
}
