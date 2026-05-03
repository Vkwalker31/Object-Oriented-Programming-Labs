package com.example.bookexchange.api.dto;

import com.example.bookexchange.models.ExchangeStatus;
import jakarta.validation.constraints.NotNull;

public record ExchangeStatusUpdateRequest(@NotNull ExchangeStatus status) {
}
