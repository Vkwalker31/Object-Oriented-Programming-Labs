package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder
public class ExchangeRequest {
    UUID id;
    UUID requesterId;
    UUID ownerId;
    UUID inventoryId;
    ExchangeStatus status;
    Instant createdAt;
    Instant updatedAt;
}
