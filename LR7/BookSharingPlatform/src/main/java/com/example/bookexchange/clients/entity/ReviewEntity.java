package com.example.bookexchange.clients.entity;

import com.example.bookexchange.models.Review;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "reviews")
public class ReviewEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID exchangeId;

    @Column(nullable = false)
    private UUID authorId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Review.TargetType targetType;

    @Column(nullable = false)
    private UUID targetId;

    @Column(nullable = false)
    private int rating;

    @Column(length = 2000)
    private String comment;

    @Column(nullable = false)
    private Instant createdAt;
}
