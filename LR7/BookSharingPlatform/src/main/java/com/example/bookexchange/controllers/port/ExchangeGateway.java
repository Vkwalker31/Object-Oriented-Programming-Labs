package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.ExchangeRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExchangeGateway {
    ExchangeRequest save(ExchangeRequest exchangeRequest);
    Optional<ExchangeRequest> findById(UUID id);
    List<ExchangeRequest> findByRequesterId(UUID requesterId);
    List<ExchangeRequest> findByOwnerId(UUID ownerId);
    List<ExchangeRequest> findByInventoryId(UUID inventoryId);
}
