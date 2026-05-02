package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.UUID;

@Value
@With
@Builder
public class Review {
    public enum TargetType {
        USER,
        BOOK
    }

    UUID id;
    UUID exchangeId;
    UUID authorId;
    TargetType targetType;
    UUID targetId;
    int rating;
    String comment;
    Instant createdAt;
}
