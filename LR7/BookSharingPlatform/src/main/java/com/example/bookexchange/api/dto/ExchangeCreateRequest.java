package com.example.bookexchange.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ExchangeCreateRequest(@NotNull UUID inventoryId) {
}
