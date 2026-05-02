package com.example.bookexchange.clients.adapter;

import com.example.bookexchange.clients.entity.BookEntity;
import com.example.bookexchange.clients.spring.BookJpaRepository;
import com.example.bookexchange.controllers.port.BookGateway;
import com.example.bookexchange.models.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookGatewayAdapter implements BookGateway {
    private final BookJpaRepository repository;

    @Override
    public Book save(Book book) {
        return toDomain(repository.save(toEntity(book)));
    }

    @Override
    public Optional<Book> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return repository.findByIsbn(isbn).map(this::toDomain);
    }

    @Override
    public List<Book> search(String title, String author, String isbn) {
        return repository.search(title, author, isbn).stream().map(this::toDomain).toList();
    }

    private BookEntity toEntity(Book book) {
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setIsbn(book.getIsbn());
        entity.setRating(book.getRating());
        return entity;
    }

    private Book toDomain(BookEntity entity) {
        return Book.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .isbn(entity.getIsbn())
                .rating(entity.getRating())
                .build();
    }
}
