package com.example.bookexchange.clients.entity;

import com.example.bookexchange.models.InventoryStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory")
public class InventoryEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID bookId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private InventoryStatus status;

    @Column(nullable = false, length = 120)
    private String conditionValue;
}
