package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewGateway {
    Review save(Review review);
    Optional<Review> findById(UUID id);
    List<Review> findByExchangeId(UUID exchangeId);
    List<Review> findByTargetTypeAndTargetId(Review.TargetType targetType, UUID targetId);
}
