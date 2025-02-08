package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findByUsers_Id(Long userId);
}