package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.BookGateway;
import com.example.bookexchange.models.Book;
import com.example.bookexchange.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookUseCaseTest {

    @Mock
    private BookGateway bookGateway;

    @InjectMocks
    private BookUseCase bookUseCase;

    @Test
    void addBook_whenIsbnExists_throwsConflict() {
        when(bookGateway.findByIsbn("9780132350884"))
                .thenReturn(Optional.of(Book.builder().id(UUID.randomUUID()).build()));

        assertThrows(ConflictException.class,
                () -> bookUseCase.addBook("Clean Code", "Robert Martin", "9780132350884"));
    }

    @Test
    void addBook_success() {
        when(bookGateway.findByIsbn("9780132350884")).thenReturn(Optional.empty());
        when(bookGateway.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        Book saved = bookUseCase.addBook("Clean Code", "Robert Martin", "9780132350884");

        assertNotNull(saved.getId());
        assertEquals("Clean Code", saved.getTitle());
        assertEquals("Robert Martin", saved.getAuthor());
        assertEquals("9780132350884", saved.getIsbn());
        assertEquals(BigDecimal.ZERO, saved.getRating());
    }
}
