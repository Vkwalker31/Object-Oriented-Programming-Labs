package com.example.bookexchange.controllers.port;

import com.example.bookexchange.models.User;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
