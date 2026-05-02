package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.util.UUID;

@Value
@With
@Builder
public class InventoryItem {
    UUID id;
    UUID userId;
    UUID bookId;
    InventoryStatus status;
    String condition;
}
