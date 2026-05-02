package com.example.bookexchange.clients.spring;

import com.example.bookexchange.clients.entity.InventoryEntity;
import com.example.bookexchange.models.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryJpaRepository extends JpaRepository<InventoryEntity, UUID> {
    List<InventoryEntity> findByUserId(UUID userId);
    Optional<InventoryEntity> findByUserIdAndBookId(UUID userId, UUID bookId);
    List<InventoryEntity> findByStatus(InventoryStatus status);
}
