package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.Book;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookGateway {
    Book save(Book book);
    Optional<Book> findById(UUID id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> search(String title, String author, String isbn);
}
