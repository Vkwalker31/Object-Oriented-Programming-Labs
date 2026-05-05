package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.ExchangeGateway;
import com.example.bookexchange.controllers.port.BookMovementGateway;
import com.example.bookexchange.controllers.port.InventoryGateway;
import com.example.bookexchange.models.ExchangeRequest;
import com.example.bookexchange.models.ExchangeStatus;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.InventoryStatus;
import com.example.bookexchange.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExchangeUseCaseTest {

    @Mock
    private ExchangeGateway exchangeGateway;
    @Mock
    private InventoryGateway inventoryGateway;
    @Mock
    private BookMovementGateway bookMovementGateway;

    @InjectMocks
    private ExchangeUseCase exchangeUseCase;

    @Test
    void requestExchange_whenInventoryNotAvailable_throwsConflict() {
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        InventoryItem inventory = InventoryItem.builder()
                .id(inventoryId)
                .userId(ownerId)
                .bookId(UUID.randomUUID())
                .status(InventoryStatus.BUSY)
                .condition("GOOD")
                .build();
        when(inventoryGateway.findById(inventoryId)).thenReturn(Optional.of(inventory));

        assertThrows(ConflictException.class, () -> exchangeUseCase.requestExchange(requesterId, inventoryId));
        verify(exchangeGateway, never()).save(any());
    }

    @Test
    void requestExchange_success() {
        UUID requesterId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        InventoryItem inventory = InventoryItem.builder()
                .id(inventoryId)
                .userId(ownerId)
                .bookId(UUID.randomUUID())
                .status(InventoryStatus.AVAILABLE)
                .condition("GOOD")
                .build();
        when(inventoryGateway.findById(inventoryId)).thenReturn(Optional.of(inventory));
        when(exchangeGateway.save(any(ExchangeRequest.class))).thenAnswer(inv -> inv.getArgument(0));
        when(inventoryGateway.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(bookMovementGateway.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ExchangeRequest created = exchangeUseCase.requestExchange(requesterId, inventoryId);

        assertEquals(requesterId, created.getRequesterId());
        assertEquals(ownerId, created.getOwnerId());
        assertEquals(ExchangeStatus.REQUESTED, created.getStatus());
        verify(inventoryGateway).save(argThat(i -> i.getStatus() == InventoryStatus.BUSY));
    }
}
