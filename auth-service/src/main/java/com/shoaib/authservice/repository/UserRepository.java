package com.shoaib.authservice.repository;

import com.shoaib.authservice.entity.User;
import com.shoaib.authservice.utility.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByIdAndIsActive(UUID email, UserStatus status);
    Optional<User> findByEmailAndIsActive(String email, UserStatus status);
}
