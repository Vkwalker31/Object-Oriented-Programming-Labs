package com.example.bookexchange.clients.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "books")
public class BookEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(unique = true, length = 32)
    private String isbn;

    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal rating;
}
