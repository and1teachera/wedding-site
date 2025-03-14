package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Long> {

    List<Family> findByNameContainingIgnoreCase(String name);
}