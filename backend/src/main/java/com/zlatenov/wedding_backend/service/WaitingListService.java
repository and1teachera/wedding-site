package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.model.WaitingList;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.RoomRepository;
import com.zlatenov.wedding_backend.repository.UserGroupRepository;
import com.zlatenov.wedding_backend.repository.WaitingListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingListService {

    private final WaitingListRepository waitingListRepository;
    private final FamilyRepository familyRepository;
    private final UserGroupRepository userGroupRepository;
    private final RoomRepository roomRepository;

    /**
     * Add a family or group to the waiting list if no rooms are available
     *
     * @param familyId The family ID (nullable if using groupId)
     * @param groupId The group ID (nullable if using familyId)
     * @param notes Additional notes for the request
     * @return true if added to waiting list, false if rooms are available
     */
    @Transactional
    public boolean addToWaitingListIfNeeded(Long familyId, Long groupId, String notes) {
        // Check if at least one of familyId or groupId is provided
        if (familyId == null && groupId == null) {
            throw new IllegalArgumentException("Either family ID or group ID must be provided");
        }

        // Check if both familyId and groupId are provided
        if (familyId != null && groupId != null) {
            throw new IllegalArgumentException("Cannot provide both family ID and group ID");
        }

        // Check if rooms are available
        int availableRooms = roomRepository.findByIsAvailableTrue().size();
        if (availableRooms > 0) {
            log.info("Rooms are available, no need to add to waiting list. Available rooms: {}", availableRooms);
            return false;
        }

        // Create waiting list entry
        WaitingList waitingListEntry = WaitingList.builder()
                .requestDate(LocalDateTime.now())
                .notificationSent(false)
                .notes(notes)
                .build();

        // Set either family or group
        if (familyId != null) {
            familyRepository.findById(familyId).ifPresent(waitingListEntry::setFamily);
        } else {
            userGroupRepository.findById(groupId).ifPresent(waitingListEntry::setGroup);
        }

        // Save to database
        waitingListRepository.save(waitingListEntry);

        log.info("Added to waiting list: {}",
                familyId != null ? "Family ID: " + familyId : "Group ID: " + groupId);

        return true;
    }
}