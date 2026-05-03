package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.BookGateway;
import com.example.bookexchange.controllers.port.InventoryGateway;
import com.example.bookexchange.controllers.port.UserGateway;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryUseCase {
    private final InventoryGateway inventoryGateway;
    private final UserGateway userGateway;
    private final BookGateway bookGateway;

    public InventoryItem addToInventory(UUID userId, UUID bookId, String condition) {
        userGateway.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        bookGateway.findById(bookId).orElseThrow(() -> new NotFoundException("Book not found"));
        if (inventoryGateway.findByUserIdAndBookId(userId, bookId).isPresent()) {
            throw new ConflictException("Book already exists in inventory");
        }
        InventoryItem item = InventoryItem.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .bookId(bookId)
                .status(InventoryStatus.AVAILABLE)
                .condition(condition)
                .build();
        InventoryItem saved = inventoryGateway.save(item);
        log.info("Inventory item created: {}", saved.getId());
        return saved;
    }

    public InventoryItem getInventoryItem(UUID inventoryId) {
        return inventoryGateway.findById(inventoryId)
                .orElseThrow(() -> new NotFoundException("Inventory item not found"));
    }

    public List<InventoryItem> getUserInventory(UUID userId) {
        return inventoryGateway.findByUserId(userId);
    }

    public InventoryItem updateStatus(UUID inventoryId, InventoryStatus status) {
        InventoryItem current = getInventoryItem(inventoryId);
        InventoryItem updated = current.withStatus(status);
        return inventoryGateway.save(updated);
    }
}
