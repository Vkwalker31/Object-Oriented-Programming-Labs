package com.example.bookexchange.clients.spring;

import com.example.bookexchange.clients.entity.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookJpaRepository extends JpaRepository<BookEntity, UUID> {
    Optional<BookEntity> findByIsbn(String isbn);

    @Query("SELECT b FROM BookEntity b WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
            "(:isbn IS NULL OR b.isbn = :isbn)")
    List<BookEntity> search(@Param("title") String title,
                            @Param("author") String author,
                            @Param("isbn") String isbn);
}
