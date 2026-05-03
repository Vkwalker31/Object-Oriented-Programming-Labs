package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.Review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID exchangeId,
        UUID authorId,
        Review.TargetType targetType,
        UUID targetId,
        int rating,
        String comment,
        Instant createdAt
) {
}
