package com.booksharing.platform.model.mapper;

import com.booksharing.platform.model.dto.exchange.BookMovementResponse;
import com.booksharing.platform.model.dto.exchange.ExchangeResponse;
import com.booksharing.platform.model.entity.Book;
import com.booksharing.platform.model.entity.BookMovement;
import com.booksharing.platform.model.entity.ExchangeRequest;
import com.booksharing.platform.model.entity.User;
import com.booksharing.platform.model.entity.UserBook;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-29T02:30:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class ExchangeMapperImpl implements ExchangeMapper {

    @Autowired
    private BookMapper bookMapper;

    @Override
    public ExchangeResponse toResponse(ExchangeRequest entity) {
        if ( entity == null ) {
            return null;
        }

        ExchangeResponse.ExchangeResponseBuilder exchangeResponse = ExchangeResponse.builder();

        exchangeResponse.requesterId( entityRequesterId( entity ) );
        exchangeResponse.requesterName( entityRequesterName( entity ) );
        exchangeResponse.ownerId( entityOwnerId( entity ) );
        exchangeResponse.ownerName( entityOwnerName( entity ) );
        exchangeResponse.book( bookMapper.toResponse( entityUserBookBook( entity ) ) );
        exchangeResponse.id( entity.getId() );
        exchangeResponse.status( entity.getStatus() );
        exchangeResponse.createdAt( entity.getCreatedAt() );

        return exchangeResponse.build();
    }

    @Override
    public BookMovementResponse toMovementResponse(BookMovement entity) {
        if ( entity == null ) {
            return null;
        }

        BookMovementResponse.BookMovementResponseBuilder bookMovementResponse = BookMovementResponse.builder();

        bookMovementResponse.fromUserId( entityFromUserId( entity ) );
        bookMovementResponse.toUserId( entityToUserId( entity ) );
        bookMovementResponse.bookTitle( entityUserBookBookTitle( entity ) );
        bookMovementResponse.id( entity.getId() );
        bookMovementResponse.movedAt( entity.getMovedAt() );

        return bookMovementResponse.build();
    }

    private UUID entityRequesterId(ExchangeRequest exchangeRequest) {
        if ( exchangeRequest == null ) {
            return null;
        }
        User requester = exchangeRequest.getRequester();
        if ( requester == null ) {
            return null;
        }
        UUID id = requester.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityRequesterName(ExchangeRequest exchangeRequest) {
        if ( exchangeRequest == null ) {
            return null;
        }
        User requester = exchangeRequest.getRequester();
        if ( requester == null ) {
            return null;
        }
        String name = requester.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private UUID entityOwnerId(ExchangeRequest exchangeRequest) {
        if ( exchangeRequest == null ) {
            return null;
        }
        User owner = exchangeRequest.getOwner();
        if ( owner == null ) {
            return null;
        }
        UUID id = owner.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityOwnerName(ExchangeRequest exchangeRequest) {
        if ( exchangeRequest == null ) {
            return null;
        }
        User owner = exchangeRequest.getOwner();
        if ( owner == null ) {
            return null;
        }
        String name = owner.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Book entityUserBookBook(ExchangeRequest exchangeRequest) {
        if ( exchangeRequest == null ) {
            return null;
        }
        UserBook userBook = exchangeRequest.getUserBook();
        if ( userBook == null ) {
            return null;
        }
        Book book = userBook.getBook();
        if ( book == null ) {
            return null;
        }
        return book;
    }

    private UUID entityFromUserId(BookMovement bookMovement) {
        if ( bookMovement == null ) {
            return null;
        }
        User fromUser = bookMovement.getFromUser();
        if ( fromUser == null ) {
            return null;
        }
        UUID id = fromUser.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID entityToUserId(BookMovement bookMovement) {
        if ( bookMovement == null ) {
            return null;
        }
        User toUser = bookMovement.getToUser();
        if ( toUser == null ) {
            return null;
        }
        UUID id = toUser.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityUserBookBookTitle(BookMovement bookMovement) {
        if ( bookMovement == null ) {
            return null;
        }
        UserBook userBook = bookMovement.getUserBook();
        if ( userBook == null ) {
            return null;
        }
        Book book = userBook.getBook();
        if ( book == null ) {
            return null;
        }
        String title = book.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }
}
