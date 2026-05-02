package com.example.bookexchange.clients.entity;

import com.example.bookexchange.models.ExchangeStatus;
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
@Table(name = "book_movements")
public class BookMovementEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID exchangeId;

    @Column(nullable = false)
    private UUID inventoryId;

    @Column(nullable = false)
    private UUID fromUserId;

    @Column(nullable = false)
    private UUID toUserId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ExchangeStatus status;

    @Column(nullable = false)
    private Instant createdAt;
}
