package com.example.bookexchange.clients.spring;

import com.example.bookexchange.clients.entity.BookMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookMovementJpaRepository extends JpaRepository<BookMovementEntity, UUID> {
    @Query("SELECT m FROM BookMovementEntity m WHERE m.fromUserId = :userId OR m.toUserId = :userId ORDER BY m.createdAt DESC")
    List<BookMovementEntity> findByUserId(@Param("userId") UUID userId);
}
