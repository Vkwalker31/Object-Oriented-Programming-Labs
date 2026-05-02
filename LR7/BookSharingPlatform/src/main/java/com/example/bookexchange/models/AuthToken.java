package com.example.bookexchange.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuthToken {
    String accessToken;
    String tokenType;
}
