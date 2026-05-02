package com.example.bookexchange.clients.adapter;

import com.example.bookexchange.clients.entity.InventoryEntity;
import com.example.bookexchange.clients.spring.InventoryJpaRepository;
import com.example.bookexchange.controllers.port.InventoryGateway;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InventoryGatewayAdapter implements InventoryGateway {
    private final InventoryJpaRepository repository;

    @Override
    public InventoryItem save(InventoryItem item) {
        return toDomain(repository.save(toEntity(item)));
    }

    @Override
    public Optional<InventoryItem> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<InventoryItem> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<InventoryItem> findByUserIdAndBookId(UUID userId, UUID bookId) {
        return repository.findByUserIdAndBookId(userId, bookId).map(this::toDomain);
    }

    @Override
    public List<InventoryItem> findByStatus(InventoryStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    private InventoryEntity toEntity(InventoryItem item) {
        InventoryEntity entity = new InventoryEntity();
        entity.setId(item.getId());
        entity.setUserId(item.getUserId());
        entity.setBookId(item.getBookId());
        entity.setStatus(item.getStatus());
        entity.setConditionValue(item.getCondition());
        return entity;
    }

    private InventoryItem toDomain(InventoryEntity entity) {
        return InventoryItem.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .bookId(entity.getBookId())
                .status(entity.getStatus())
                .condition(entity.getConditionValue())
                .build();
    }
}
