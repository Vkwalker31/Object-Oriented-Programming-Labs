package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.math.BigDecimal;
import java.util.UUID;

@Value
@With
@Builder
public class Book {
    UUID id;
    String title;
    String author;
    String isbn;
    BigDecimal rating;
}
