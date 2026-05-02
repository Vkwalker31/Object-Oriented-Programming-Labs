package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryGateway {
    InventoryItem save(InventoryItem item);
    Optional<InventoryItem> findById(UUID id);
    List<InventoryItem> findByUserId(UUID userId);
    Optional<InventoryItem> findByUserIdAndBookId(UUID userId, UUID bookId);
    List<InventoryItem> findByStatus(InventoryStatus status);
}
