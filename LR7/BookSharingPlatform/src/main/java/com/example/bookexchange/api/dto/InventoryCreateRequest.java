package com.example.bookexchange.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InventoryCreateRequest(
        @NotNull UUID bookId,
        @NotBlank String condition
) {
}
