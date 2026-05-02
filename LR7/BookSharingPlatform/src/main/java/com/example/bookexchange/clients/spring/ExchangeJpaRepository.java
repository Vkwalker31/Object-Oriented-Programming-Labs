package com.example.bookexchange.clients.spring;

import com.example.bookexchange.clients.entity.ExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExchangeJpaRepository extends JpaRepository<ExchangeEntity, UUID> {
    List<ExchangeEntity> findByRequesterId(UUID requesterId);
    List<ExchangeEntity> findByOwnerId(UUID ownerId);
    List<ExchangeEntity> findByInventoryId(UUID inventoryId);
}
