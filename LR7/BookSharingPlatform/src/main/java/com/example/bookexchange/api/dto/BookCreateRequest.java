package com.example.bookexchange.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BookCreateRequest(
        @NotBlank @Size(max = 500) String title,
        @NotBlank @Size(max = 255) String author,
        @Size(max = 32) String isbn
) {
}
