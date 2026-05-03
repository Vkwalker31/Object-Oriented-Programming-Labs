package com.example.bookexchange.api.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email, BigDecimal rating) {
}
