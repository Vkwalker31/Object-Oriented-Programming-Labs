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
import com.example.bookexchange.models.InventoryStatus;
import com.example.bookexchange.models.Review;
import com.example.bookexchange.models.User;
import com.example.bookexchange.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewUseCaseTest {

    @Mock
    private ReviewGateway reviewGateway;
    @Mock
    private ExchangeGateway exchangeGateway;
    @Mock
    private UserGateway userGateway;
    @Mock
    private BookGateway bookGateway;
    @Mock
    private InventoryGateway inventoryGateway;

    @InjectMocks
    private ReviewUseCase reviewUseCase;

    @Test
    void leaveReview_whenExchangeNotSuccessful_throwsConflict() {
        UUID reviewerId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();
        ExchangeRequest exchange = ExchangeRequest.builder()
                .id(exchangeId)
                .requesterId(reviewerId)
                .ownerId(UUID.randomUUID())
                .inventoryId(UUID.randomUUID())
                .status(ExchangeStatus.REQUESTED)
                .build();
        when(exchangeGateway.findById(exchangeId)).thenReturn(Optional.of(exchange));

        assertThrows(ConflictException.class,
                () -> reviewUseCase.leaveReview(reviewerId, exchangeId, Review.TargetType.USER, 5, "Great exchange"));
    }

    @Test
    void leaveReview_whenRatingOutOfRange_throwsConflict() {
        UUID reviewerId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();

        assertThrows(ConflictException.class,
                () -> reviewUseCase.leaveReview(reviewerId, exchangeId, Review.TargetType.USER, 6, "invalid"));
    }

    @Test
    void leaveReview_success_recalculatesRating() {
        UUID reviewerId = UUID.randomUUID();
        UUID revieweeId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();

        ExchangeRequest exchange = ExchangeRequest.builder()
                .id(exchangeId)
                .requesterId(reviewerId)
                .ownerId(revieweeId)
                .inventoryId(UUID.randomUUID())
                .status(ExchangeStatus.TRANSFERRED)
                .build();
        User reviewee = User.builder()
                .id(revieweeId)
                .username("owner")
                .email("owner@mail.com")
                .passwordHash("hash")
                .rating(BigDecimal.ZERO)
                .build();
        when(exchangeGateway.findById(exchangeId)).thenReturn(Optional.of(exchange));
        when(reviewGateway.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userGateway.findById(revieweeId)).thenReturn(Optional.of(reviewee));
        when(reviewGateway.findByTargetTypeAndTargetId(Review.TargetType.USER, revieweeId)).thenReturn(List.of(
                Review.builder().rating(4).build(),
                Review.builder().rating(5).build()
        ));
        when(userGateway.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        reviewUseCase.leaveReview(reviewerId, exchangeId, Review.TargetType.USER, 5, "Great exchange");
    }

    @Test
    void leaveReview_bookTarget_recalculatesBookRating() {
        UUID reviewerId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID exchangeId = UUID.randomUUID();
        UUID inventoryId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();

        ExchangeRequest exchange = ExchangeRequest.builder()
                .id(exchangeId)
                .requesterId(reviewerId)
                .ownerId(ownerId)
                .inventoryId(inventoryId)
                .status(ExchangeStatus.COMPLETED)
                .build();
        InventoryItem item = InventoryItem.builder()
                .id(inventoryId)
                .userId(ownerId)
                .bookId(bookId)
                .status(InventoryStatus.AVAILABLE)
                .condition("GOOD")
                .build();
        Book book = Book.builder()
                .id(bookId)
                .title("Clean Architecture")
                .author("Robert C. Martin")
                .isbn("123")
                .rating(BigDecimal.ZERO)
                .build();

        when(exchangeGateway.findById(exchangeId)).thenReturn(Optional.of(exchange));
        when(inventoryGateway.findById(inventoryId)).thenReturn(Optional.of(item));
        when(reviewGateway.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reviewGateway.findByTargetTypeAndTargetId(Review.TargetType.BOOK, bookId)).thenReturn(List.of(
                Review.builder().rating(3).build(),
                Review.builder().rating(5).build()
        ));
        when(bookGateway.findById(bookId)).thenReturn(Optional.of(book));
        when(bookGateway.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

        reviewUseCase.leaveReview(reviewerId, exchangeId, Review.TargetType.BOOK, 4, "Useful book");
    }
}
