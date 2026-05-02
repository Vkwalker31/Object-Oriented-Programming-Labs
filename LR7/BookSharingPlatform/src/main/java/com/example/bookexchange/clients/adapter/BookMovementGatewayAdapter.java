package com.example.bookexchange.clients.adapter;

import com.example.bookexchange.clients.entity.BookMovementEntity;
import com.example.bookexchange.clients.spring.BookMovementJpaRepository;
import com.example.bookexchange.controllers.port.BookMovementGateway;
import com.example.bookexchange.models.BookMovement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookMovementGatewayAdapter implements BookMovementGateway {
    private final BookMovementJpaRepository repository;

    @Override
    public BookMovement save(BookMovement movement) {
        return toDomain(repository.save(toEntity(movement)));
    }

    @Override
    public List<BookMovement> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    private BookMovementEntity toEntity(BookMovement movement) {
        BookMovementEntity entity = new BookMovementEntity();
        entity.setId(movement.getId());
        entity.setExchangeId(movement.getExchangeId());
        entity.setInventoryId(movement.getInventoryId());
        entity.setFromUserId(movement.getFromUserId());
        entity.setToUserId(movement.getToUserId());
        entity.setStatus(movement.getStatus());
        entity.setCreatedAt(movement.getCreatedAt());
        return entity;
    }

    private BookMovement toDomain(BookMovementEntity entity) {
        return BookMovement.builder()
                .id(entity.getId())
                .exchangeId(entity.getExchangeId())
                .inventoryId(entity.getInventoryId())
                .fromUserId(entity.getFromUserId())
                .toUserId(entity.getToUserId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
