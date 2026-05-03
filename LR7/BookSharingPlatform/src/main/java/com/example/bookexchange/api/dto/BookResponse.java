package com.example.bookexchange.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record BookResponse(UUID id, String title, String author, String isbn, BigDecimal rating) {
}
