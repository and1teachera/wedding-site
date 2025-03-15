package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByFamilyId(Long familyId);
    
    List<User> findByFamilyIsNotNull();
    
    List<User> findByFamilyIsNull();

    Optional<User> findByFirstNameAndLastName(String firstName, String lastName);
}