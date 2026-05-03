package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.ExchangeGateway;
import com.example.bookexchange.controllers.port.BookMovementGateway;
import com.example.bookexchange.controllers.port.InventoryGateway;
import com.example.bookexchange.models.BookMovement;
import com.example.bookexchange.models.ExchangeRequest;
import com.example.bookexchange.models.ExchangeStatus;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExchangeUseCase {
    private final ExchangeGateway exchangeGateway;
    private final InventoryGateway inventoryGateway;
    private final BookMovementGateway bookMovementGateway;

    public ExchangeRequest requestExchange(UUID requesterId, UUID inventoryId) {
        InventoryItem item = inventoryGateway.findById(inventoryId)
                .orElseThrow(() -> new NotFoundException("Inventory item not found"));
        if (item.getUserId().equals(requesterId)) {
            throw new ConflictException("Cannot request your own book");
        }
        if (item.getStatus() != InventoryStatus.AVAILABLE) {
            throw new ConflictException("Book is not available for exchange");
        }
        Instant now = Instant.now();
        ExchangeRequest request = ExchangeRequest.builder()
                .id(UUID.randomUUID())
                .requesterId(requesterId)
                .ownerId(item.getUserId())
                .inventoryId(inventoryId)
                .status(ExchangeStatus.REQUESTED)
                .createdAt(now)
                .updatedAt(now)
                .build();
        ExchangeRequest saved = exchangeGateway.save(request);
        inventoryGateway.save(item.withStatus(InventoryStatus.BUSY));
        recordMovement(saved, item, ExchangeStatus.REQUESTED);
        log.info("Exchange requested: {}", saved.getId());
        return saved;
    }

    public ExchangeRequest updateStatus(UUID exchangeId, ExchangeStatus nextStatus) {
        ExchangeRequest exchange = exchangeGateway.findById(exchangeId)
                .orElseThrow(() -> new NotFoundException("Exchange request not found"));
        ExchangeRequest updated = exchange.withStatus(nextStatus).withUpdatedAt(Instant.now());
        ExchangeRequest saved = exchangeGateway.save(updated);

        if (nextStatus == ExchangeStatus.COMPLETED) {
            inventoryGateway.findById(saved.getInventoryId()).ifPresent(item ->
                    inventoryGateway.save(item.withStatus(InventoryStatus.AVAILABLE)));
        }
        inventoryGateway.findById(saved.getInventoryId()).ifPresent(item -> recordMovement(saved, item, nextStatus));
        log.info("Exchange {} status changed to {}", exchangeId, nextStatus);
        return saved;
    }

    public List<ExchangeRequest> getIncoming(UUID ownerId) {
        return exchangeGateway.findByOwnerId(ownerId);
    }

    public List<ExchangeRequest> getOutgoing(UUID requesterId) {
        return exchangeGateway.findByRequesterId(requesterId);
    }

    public ExchangeRequest getById(UUID exchangeId) {
        return exchangeGateway.findById(exchangeId)
                .orElseThrow(() -> new NotFoundException("Exchange request not found"));
    }

    public List<BookMovement> getHistory(UUID userId) {
        return bookMovementGateway.findByUserId(userId);
    }

    private void recordMovement(ExchangeRequest exchange, InventoryItem item, ExchangeStatus status) {
        bookMovementGateway.save(BookMovement.builder()
                .id(UUID.randomUUID())
                .exchangeId(exchange.getId())
                .inventoryId(exchange.getInventoryId())
                .fromUserId(exchange.getOwnerId())
                .toUserId(exchange.getRequesterId())
                .status(status)
                .createdAt(Instant.now())
                .build());
    }
}
