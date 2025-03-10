package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SingleUserAccommodationRepository extends JpaRepository<SingleUserAccommodationRequest, Long> {

    @Query(value = "SELECT * FROM single_user_accommodation_requests " +
            "WHERE user_id = :userId " +
            "ORDER BY request_date DESC LIMIT 1",
            nativeQuery = true)
    Optional<SingleUserAccommodationRequest> findLatestByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM single_user_accommodation_requests " +
            "WHERE user_id = :userId AND status = 'PENDING' " +
            "ORDER BY request_date DESC LIMIT 1",
            nativeQuery = true)
    Optional<SingleUserAccommodationRequest> findLatestPendingByUserId(@Param("userId") Long userId);
    
    /**
     * Find all requests ordered by request date (newest first)
     */
    List<SingleUserAccommodationRequest> findAllByOrderByRequestDateDesc();
}