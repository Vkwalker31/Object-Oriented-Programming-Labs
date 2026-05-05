package com.booksharing.platform.model.mapper;

import com.booksharing.platform.model.dto.review.ReviewResponse;
import com.booksharing.platform.model.entity.Review;
import com.booksharing.platform.model.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-29T02:30:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public ReviewResponse toResponse(Review entity) {
        if ( entity == null ) {
            return null;
        }

        ReviewResponse.ReviewResponseBuilder reviewResponse = ReviewResponse.builder();

        reviewResponse.authorName( entityAuthorName( entity ) );
        reviewResponse.id( entity.getId() );
        reviewResponse.targetType( entity.getTargetType() );
        reviewResponse.targetId( entity.getTargetId() );
        reviewResponse.rating( entity.getRating() );
        reviewResponse.text( entity.getText() );
        reviewResponse.createdAt( entity.getCreatedAt() );

        return reviewResponse.build();
    }

    private String entityAuthorName(Review review) {
        if ( review == null ) {
            return null;
        }
        User author = review.getAuthor();
        if ( author == null ) {
            return null;
        }
        String name = author.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
