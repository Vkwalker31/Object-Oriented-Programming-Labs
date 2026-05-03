package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.BookGateway;
import com.example.bookexchange.models.Book;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookUseCase {
    private final BookGateway bookGateway;

    public Book addBook(String title, String author, String isbn) {
        String normalizedIsbn = isbn == null ? null : isbn.trim();
        if (normalizedIsbn != null && !normalizedIsbn.isBlank() && bookGateway.findByIsbn(normalizedIsbn).isPresent()) {
            throw new ConflictException("Book with this ISBN already exists");
        }
        Book book = Book.builder()
                .id(UUID.randomUUID())
                .title(title)
                .author(author)
                .isbn(normalizedIsbn)
                .rating(BigDecimal.ZERO)
                .build();
        Book saved = bookGateway.save(book);
        log.info("Book added to catalog: {}", saved.getId());
        return saved;
    }

    public Book getBook(UUID bookId) {
        return bookGateway.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
    }

    public List<Book> search(String title, String author, String isbn) {
        return bookGateway.search(title, author, isbn);
    }
}
