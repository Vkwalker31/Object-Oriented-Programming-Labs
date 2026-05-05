package com.booksharing.platform.model.mapper;

import com.booksharing.platform.model.dto.userbook.UserBookResponse;
import com.booksharing.platform.model.entity.UserBook;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-04-29T02:30:15+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class UserBookMapperImpl implements UserBookMapper {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserBookResponse toResponse(UserBook entity) {
        if ( entity == null ) {
            return null;
        }

        UserBookResponse.UserBookResponseBuilder userBookResponse = UserBookResponse.builder();

        userBookResponse.user( userMapper.toResponse( entity.getUser() ) );
        userBookResponse.book( bookMapper.toResponse( entity.getBook() ) );
        userBookResponse.id( entity.getId() );
        userBookResponse.addedAt( entity.getAddedAt() );

        return userBookResponse.build();
    }
}
