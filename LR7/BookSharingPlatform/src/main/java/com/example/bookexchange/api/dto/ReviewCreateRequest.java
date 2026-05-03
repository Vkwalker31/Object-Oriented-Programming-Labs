package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ReviewCreateRequest(
        @NotNull UUID exchangeId,
        @NotNull Review.TargetType targetType,
        @Min(1) @Max(5) int rating,
        @Size(max = 2000) String comment
) {
}
