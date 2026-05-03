package com.example.bookexchange.controllers;

import com.example.bookexchange.controllers.port.ExchangeGateway;
import com.example.bookexchange.controllers.port.InventoryGateway;
import com.example.bookexchange.controllers.port.BookGateway;
import com.example.bookexchange.controllers.port.ReviewGateway;
import com.example.bookexchange.controllers.port.UserGateway;
import com.example.bookexchange.models.Book;
import com.example.bookexchange.models.ExchangeRequest;
import com.example.bookexchange.models.ExchangeStatus;
import com.example.bookexchange.models.InventoryItem;
import com.example.bookexchange.models.Review;
import com.example.bookexchange.models.User;
import com.example.bookexchange.shared.exception.ConflictException;
import com.example.bookexchange.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewUseCase {
    private final ReviewGateway reviewGateway;
    private final ExchangeGateway exchangeGateway;
    private final UserGateway userGateway;
    private final BookGateway bookGateway;
    private final InventoryGateway inventoryGateway;

    public Review leaveReview(UUID authorId, UUID exchangeId, Review.TargetType targetType, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new ConflictException("Rating must be between 1 and 5");
        }
        ExchangeRequest exchange = exchangeGateway.findById(exchangeId)
                .orElseThrow(() -> new NotFoundException("Exchange not found"));
        if (exchange.getStatus() != ExchangeStatus.TRANSFERRED && exchange.getStatus() != ExchangeStatus.COMPLETED) {
            throw new ConflictException("Review allowed only after successful exchange");
        }
        if (!exchange.getRequesterId().equals(authorId) && !exchange.getOwnerId().equals(authorId)) {
            throw new ConflictException("Author is not a participant of this exchange");
        }
        UUID targetId = resolveTargetId(exchange, authorId, targetType);

        Review review = Review.builder()
                .id(UUID.randomUUID())
                .exchangeId(exchangeId)
                .authorId(authorId)
                .targetType(targetType)
                .targetId(targetId)
                .rating(rating)
                .comment(comment)
                .createdAt(Instant.now())
                .build();
        Review saved = reviewGateway.save(review);
        recalculateRating(targetType, targetId);
        log.info("Review {} created for {} {}", saved.getId(), targetType, targetId);
        return saved;
    }

    public List<Review> getByTarget(Review.TargetType targetType, UUID targetId) {
        return reviewGateway.findByTargetTypeAndTargetId(targetType, targetId);
    }

    private UUID resolveTargetId(ExchangeRequest exchange, UUID authorId, Review.TargetType targetType) {
        if (targetType == Review.TargetType.USER) {
            return exchange.getRequesterId().equals(authorId)
                    ? exchange.getOwnerId()
                    : exchange.getRequesterId();
        }
        InventoryItem item = inventoryGateway.findById(exchange.getInventoryId())
                .orElseThrow(() -> new NotFoundException("Inventory item not found"));
        return item.getBookId();
    }

    private void recalculateRating(Review.TargetType targetType, UUID targetId) {
        List<Review> reviews = reviewGateway.findByTargetTypeAndTargetId(targetType, targetId);
        if (reviews.isEmpty()) {
            setZeroRating(targetType, targetId);
            return;
        }
        BigDecimal avg = BigDecimal.valueOf(
                        reviews.stream().mapToInt(Review::getRating).average().orElse(0.0))
                .setScale(2, RoundingMode.HALF_UP);
        if (targetType == Review.TargetType.USER) {
            User user = userGateway.findById(targetId).orElseThrow(() -> new NotFoundException("User not found"));
            userGateway.save(user.withRating(avg));
        } else {
            Book book = bookGateway.findById(targetId).orElseThrow(() -> new NotFoundException("Book not found"));
            bookGateway.save(book.withRating(avg));
        }
    }

    private void setZeroRating(Review.TargetType targetType, UUID targetId) {
        if (targetType == Review.TargetType.USER) {
            User user = userGateway.findById(targetId).orElseThrow(() -> new NotFoundException("User not found"));
            userGateway.save(user.withRating(BigDecimal.ZERO));
        } else {
            Book book = bookGateway.findById(targetId).orElseThrow(() -> new NotFoundException("Book not found"));
            bookGateway.save(book.withRating(BigDecimal.ZERO));
        }
    }
}
