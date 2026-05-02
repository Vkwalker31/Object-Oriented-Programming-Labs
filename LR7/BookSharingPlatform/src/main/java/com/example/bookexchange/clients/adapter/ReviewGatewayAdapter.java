package com.example.bookexchange.clients.adapter;

import com.example.bookexchange.clients.entity.ReviewEntity;
import com.example.bookexchange.clients.spring.ReviewJpaRepository;
import com.example.bookexchange.controllers.port.ReviewGateway;
import com.example.bookexchange.models.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewGatewayAdapter implements ReviewGateway {
    private final ReviewJpaRepository repository;

    @Override
    public Review save(Review review) {
        return toDomain(repository.save(toEntity(review)));
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Review> findByExchangeId(UUID exchangeId) {
        return repository.findByExchangeId(exchangeId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Review> findByTargetTypeAndTargetId(Review.TargetType targetType, UUID targetId) {
        return repository.findByTargetTypeAndTargetId(targetType, targetId).stream().map(this::toDomain).toList();
    }

    private ReviewEntity toEntity(Review review) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(review.getId());
        entity.setExchangeId(review.getExchangeId());
        entity.setAuthorId(review.getAuthorId());
        entity.setTargetType(review.getTargetType());
        entity.setTargetId(review.getTargetId());
        entity.setRating(review.getRating());
        entity.setComment(review.getComment());
        entity.setCreatedAt(review.getCreatedAt());
        return entity;
    }

    private Review toDomain(ReviewEntity entity) {
        return Review.builder()
                .id(entity.getId())
                .exchangeId(entity.getExchangeId())
                .authorId(entity.getAuthorId())
                .targetType(entity.getTargetType())
                .targetId(entity.getTargetId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
