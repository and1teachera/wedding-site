package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {
    Optional<UserResponse> findByUserId(Long userId);
}