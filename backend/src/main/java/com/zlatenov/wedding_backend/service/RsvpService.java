package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.*;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.UserRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RsvpService {

    private final UserRepository userRepository;
    private final UserResponseRepository userResponseRepository;
    private final WaitingListService waitingListService;

    /**
     * Process an RSVP submission
     *
     * @param request The RSVP request containing guest responses
     * @param username The authenticated username (first and last name)
     * @return Response with success status and information
     */
    @Transactional
    public RsvpResponse processRsvp(RsvpRequest request, String username) {
        // Get the authenticated user and family
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }

        User primaryUser = userRepository.findByFirstNameAndLastName(names[0], names[1])
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify this is the primary user's response
        if (!Objects.equals(primaryUser.getId(), request.getPrimaryGuest().getUserId())) {
            throw new UnauthorizedAccessException("Unauthorized access to user data");
        }

        // Update primary guest's response
        updateUserResponse(request.getPrimaryGuest());

        // Process family members if present
        int confirmedAttendees = request.getPrimaryGuest().getStatus() == ResponseStatus.YES ? 1 : 0;

        if (request.getFamilyMembers() != null && !request.getFamilyMembers().isEmpty()) {
            // Verify all family members belong to the same family
            verifyFamilyMembersAccess(primaryUser, request.getFamilyMembers());

            // Update each family member's response
            for (GuestResponse memberResponse : request.getFamilyMembers()) {
                updateUserResponse(memberResponse);
                if (memberResponse.getStatus() == ResponseStatus.YES) {
                    confirmedAttendees++;
                }
            }
        }

        log.info("Successfully processed RSVP for family. Primary user: {}, confirmed attendees: {}",
                username, confirmedAttendees);

        return RsvpResponse.builder()
                .success(true)
                .message("RSVP has been successfully recorded")
                .primaryUserId(primaryUser.getId())
                .confirmedAttendees(confirmedAttendees)
                .build();
    }

    /**
     * Get the current RSVP status for a user and their family
     *
     * @param username The authenticated username
     * @return Response with the current RSVP status
     */
    @Transactional(readOnly = true)
    public RsvpResponse getRsvpStatus(String username) {
        // Get the authenticated user
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }

        User primaryUser = userRepository.findByFirstNameAndLastName(names[0], names[1])
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get primary user's response
        UserResponse primaryResponse = userResponseRepository.findByUserId(primaryUser.getId())
                .orElse(null);

        // Count confirmed attendees
        int confirmedAttendees = 0;
        if (primaryResponse != null && primaryResponse.getStatus() == ResponseStatus.YES) {
            confirmedAttendees = 1;
        }

        // If user has a family, get family responses
        if (primaryUser.getFamily() != null) {
            List<User> familyMembers = userRepository.findByFamilyId(primaryUser.getFamily().getId());

            for (User member : familyMembers) {
                if (Objects.equals(member.getId(), primaryUser.getId())) {
                    continue; // Skip primary user
                }

                UserResponse memberResponse = userResponseRepository.findByUserId(member.getId())
                        .orElse(null);

                if (memberResponse != null && memberResponse.getStatus() == ResponseStatus.YES) {
                    confirmedAttendees++;
                }
            }
        }

        return RsvpResponse.builder()
                .success(true)
                .message("Current RSVP status retrieved")
                .primaryUserId(primaryUser.getId())
                .confirmedAttendees(confirmedAttendees)
                .build();
    }

    /**
     * Get family members for the RSVP form
     *
     * @param username The authenticated username
     * @return Response with primary user and family members information
     */
    @Transactional(readOnly = true)
    public FamilyMembersResponse getFamilyMembers(String username) {
        // Get the authenticated user
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }

        User primaryUser = userRepository.findByFirstNameAndLastName(names[0], names[1])
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get primary user's response
        UserResponse primaryResponse = userResponseRepository.findByUserId(primaryUser.getId())
                .orElse(null);

        UserDto primaryUserDto = createUserDtoFromResponse(primaryUser, primaryResponse);

        List<UserDto> familyMembersDto = new ArrayList<>();

        // If user has a family, get family members
        if (primaryUser.getFamily() != null) {
            List<User> familyMembers = userRepository.findByFamilyId(primaryUser.getFamily().getId());

            for (User member : familyMembers) {
                if (Objects.equals(member.getId(), primaryUser.getId())) {
                    continue; // Skip primary user
                }

                UserResponse memberResponse = userResponseRepository.findByUserId(member.getId())
                        .orElse(null);

                UserDto memberDto = createUserDtoFromResponse(member, memberResponse);

                familyMembersDto.add(memberDto);
            }
        }

        return FamilyMembersResponse.builder()
                .primaryUser(primaryUserDto)
                .familyMembers(familyMembersDto)
                .build();
    }

    private UserDto createUserDtoFromResponse(User user, UserResponse userResponse) {
        return UserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isChild(user.isChild())
                .rsvpStatus(userResponse != null ? userResponse.getStatus() : ResponseStatus.MAYBE)
                .dietaryNotes(userResponse != null ? userResponse.getDietaryNotes() : null)
                .additionalNotes(userResponse != null ? userResponse.getAdditionalNotes() : null)
                .build();
    }

    /**
     * Verify that the authenticated user has access to update the specified user
     */
    private void verifyUserAccess(User authenticatedUser, Long requestUserId) {
        if (!Objects.equals(authenticatedUser.getId(), requestUserId)) {
            throw new UnauthorizedAccessException("Unauthorized access to user data");
        }
    }

    /**
     * Verify that the authenticated user has access to update family members
     */
    private void verifyFamilyMembersAccess(User primaryUser, List<GuestResponse> familyMembers) {
        if (primaryUser.getFamily() == null) {
            throw new UnauthorizedAccessException("User does not have a family");
        }

        Family family = primaryUser.getFamily();
        Set<Long> familyMemberIds = userRepository.findByFamilyId(family.getId())
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        for (GuestResponse member : familyMembers) {
            if (!familyMemberIds.contains(member.getUserId())) {
                throw new UnauthorizedAccessException("Unauthorized access to family member data");
            }
        }
    }

    /**
     * Update a user's response status
     */
    private void updateUserResponse(GuestResponse guestResponse) {
        User user = userRepository.findById(guestResponse.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserResponse response = userResponseRepository.findByUserId(user.getId())
                .orElse(UserResponse.builder()
                        .user(user)
                        .build());

        response.setStatus(guestResponse.getStatus());
        response.setDietaryNotes(guestResponse.getDietaryNotes());
        response.setAdditionalNotes(guestResponse.getAdditionalNotes());

        userResponseRepository.save(response);
        log.debug("Updated response for user ID: {}, status: {}", user.getId(), response.getStatus());
    }

    /**
     * Process accommodation request for a family
     */
    private void processAccommodationRequest(User primaryUser, AccommodationRequest accommodationRequest, int confirmedAttendees) {
        // This is a placeholder for room booking/waiting list logic
        // In a real implementation, you would call the appropriate service
        if (primaryUser.getFamily() != null) {
            waitingListService.addToWaitingListIfNeeded(primaryUser.getFamily().getId(), null, accommodationRequest.getNotes());
            log.info("Processed accommodation request for family: {}", primaryUser.getFamily().getId());
        }
    }

    /**
     * Save the primary guest's response only
     * @param guestResponse The primary guest's response
     * @param username The authenticated user's username
     * @return Response with success status
     */
    @Transactional
    public RsvpResponse savePrimaryGuestResponse(GuestResponse guestResponse, String username) {
        // Get the authenticated user
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException("Invalid username format");
        }

        User primaryUser = userRepository.findByFirstNameAndLastName(names[0], names[1])
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Verify this is the primary user's response
        if (!Objects.equals(primaryUser.getId(), guestResponse.getUserId())) {
            throw new UnauthorizedAccessException("Unauthorized access to user data");
        }

        // Update the user's response
        UserResponse response = userResponseRepository.findByUserId(primaryUser.getId())
                .orElse(UserResponse.builder()
                        .user(primaryUser)
                        .build());

        response.setStatus(guestResponse.getStatus());
        response.setDietaryNotes(guestResponse.getDietaryNotes());
        response.setAdditionalNotes(guestResponse.getAdditionalNotes());

        userResponseRepository.save(response);

        log.info("Saved primary guest response for user: {}, status: {}",
                username, response.getStatus());

        return RsvpResponse.builder()
                .success(true)
                .message("Primary guest response saved successfully")
                .primaryUserId(primaryUser.getId())
                .confirmedAttendees(response.getStatus() == ResponseStatus.YES ? 1 : 0)
                .build();
    }

}