package com.example.bookexchange.clients.adapter;

import com.example.bookexchange.clients.entity.ExchangeEntity;
import com.example.bookexchange.clients.spring.ExchangeJpaRepository;
import com.example.bookexchange.controllers.port.ExchangeGateway;
import com.example.bookexchange.models.ExchangeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ExchangeGatewayAdapter implements ExchangeGateway {
    private final ExchangeJpaRepository repository;

    @Override
    public ExchangeRequest save(ExchangeRequest exchangeRequest) {
        return toDomain(repository.save(toEntity(exchangeRequest)));
    }

    @Override
    public Optional<ExchangeRequest> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ExchangeRequest> findByRequesterId(UUID requesterId) {
        return repository.findByRequesterId(requesterId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ExchangeRequest> findByOwnerId(UUID ownerId) {
        return repository.findByOwnerId(ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<ExchangeRequest> findByInventoryId(UUID inventoryId) {
        return repository.findByInventoryId(inventoryId).stream().map(this::toDomain).toList();
    }

    private ExchangeEntity toEntity(ExchangeRequest request) {
        ExchangeEntity entity = new ExchangeEntity();
        entity.setId(request.getId());
        entity.setRequesterId(request.getRequesterId());
        entity.setOwnerId(request.getOwnerId());
        entity.setInventoryId(request.getInventoryId());
        entity.setStatus(request.getStatus());
        entity.setCreatedAt(request.getCreatedAt());
        entity.setUpdatedAt(request.getUpdatedAt());
        return entity;
    }

    private ExchangeRequest toDomain(ExchangeEntity entity) {
        return ExchangeRequest.builder()
                .id(entity.getId())
                .requesterId(entity.getRequesterId())
                .ownerId(entity.getOwnerId())
                .inventoryId(entity.getInventoryId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
