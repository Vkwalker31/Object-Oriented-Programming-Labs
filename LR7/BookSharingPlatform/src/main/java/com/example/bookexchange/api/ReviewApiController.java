package com.example.bookexchange.api;

import com.example.bookexchange.api.dto.ReviewCreateRequest;
import com.example.bookexchange.api.dto.ReviewResponse;
import com.example.bookexchange.controllers.ReviewUseCase;
import com.example.bookexchange.models.Review;
import com.example.bookexchange.shared.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews")
public class ReviewApiController {
    private final ReviewUseCase reviewUseCase;

    @PostMapping
    @Operation(summary = "Leave review after successful exchange")
    public ResponseEntity<ApiResponse<ReviewResponse>> create(Authentication authentication,
                                                              @Valid @RequestBody ReviewCreateRequest request) {
        UUID authorId = (UUID) authentication.getPrincipal();
        Review review = reviewUseCase.leaveReview(
                authorId,
                request.exchangeId(),
                request.targetType(),
                request.rating(),
                request.comment()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(toResponse(review)));
    }

    @GetMapping
    @Operation(summary = "Get reviews by target")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> byTarget(@RequestParam Review.TargetType targetType,
                                                                      @RequestParam UUID targetId) {
        List<ReviewResponse> response = reviewUseCase.getByTarget(targetType, targetId).stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getExchangeId(),
                review.getAuthorId(),
                review.getTargetType(),
                review.getTargetId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt()
        );
    }
}
