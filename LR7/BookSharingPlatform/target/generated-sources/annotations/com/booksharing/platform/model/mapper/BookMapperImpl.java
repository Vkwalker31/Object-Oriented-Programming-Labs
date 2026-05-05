package com.booksharing.platform.model.mapper;

import com.booksharing.platform.model.dto.book.BookResponse;
import com.booksharing.platform.model.entity.Book;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-29T02:30:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookResponse toResponse(Book entity) {
        if ( entity == null ) {
            return null;
        }

        BookResponse.BookResponseBuilder bookResponse = BookResponse.builder();

        bookResponse.id( entity.getId() );
        bookResponse.title( entity.getTitle() );
        bookResponse.author( entity.getAuthor() );
        bookResponse.isbn( entity.getIsbn() );
        bookResponse.createdAt( entity.getCreatedAt() );

        return bookResponse.build();
    }
}
