package com.example.bookexchange.clients.spring;

import com.example.bookexchange.clients.entity.ReviewEntity;
import com.example.bookexchange.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, UUID> {
    List<ReviewEntity> findByExchangeId(UUID exchangeId);
    List<ReviewEntity> findByTargetTypeAndTargetId(Review.TargetType targetType, UUID targetId);
}
