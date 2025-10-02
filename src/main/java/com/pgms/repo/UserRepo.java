package com.pgms.repo;

import com.pgms.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    default Optional<User> findByLoginId(String login) {
        if (login == null) return Optional.empty();
        var key = login.trim().toLowerCase();
        var byEmail = findByEmail(key);
        return byEmail.isPresent() ? byEmail : findByPhone(key);
    }
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
