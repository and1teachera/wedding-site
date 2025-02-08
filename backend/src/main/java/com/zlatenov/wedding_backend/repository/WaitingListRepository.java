package com.zlatenov.wedding_backend.repository;

import com.zlatenov.wedding_backend.model.WaitingList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingList, Long> {
    List<WaitingList> findByFamilyId(Long familyId);
    List<WaitingList> findByGroupId(Long groupId);
}