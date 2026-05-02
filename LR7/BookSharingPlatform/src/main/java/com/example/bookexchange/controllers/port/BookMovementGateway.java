package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.BookMovement;

import java.util.List;
import java.util.UUID;

public interface BookMovementGateway {
    BookMovement save(BookMovement movement);
    List<BookMovement> findByUserId(UUID userId);
}
