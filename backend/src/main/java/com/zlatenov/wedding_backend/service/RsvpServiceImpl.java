package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.AccommodationRequest;
import com.zlatenov.wedding_backend.dto.AllRsvpResponsesDto;
import com.zlatenov.wedding_backend.dto.FamilyMembersResponse;
import com.zlatenov.wedding_backend.dto.GuestResponse;
import com.zlatenov.wedding_backend.dto.RsvpRequest;
import com.zlatenov.wedding_backend.dto.RsvpResponse;
import com.zlatenov.wedding_backend.dto.UserDto;
import com.zlatenov.wedding_backend.exception.UnauthorizedAccessException;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RsvpServiceImpl implements RsvpService {

    public static final String INVALID_USERNAME_FORMAT = "Invalid username format";
    public static final String UNAUTHORIZED_ACCESS_TO_USER_DATA = "Unauthorized access to user data";

    private final UserService userService;
    private final UserResponseRepository userResponseRepository;
    private final WaitingListService waitingListService;

    @Transactional
    @Override
    public RsvpResponse processRsvp(RsvpRequest request, String username) {
        User primaryUser = getPrimaryUserByUsername(username);

        // Verify this is the primary user's response
        if (!Objects.equals(primaryUser.getId(), request.getPrimaryGuest().getUserId())) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS_TO_USER_DATA);
        }

        updateUserResponse(request.getPrimaryGuest());

        int confirmedAttendees = request.getPrimaryGuest().getStatus() == ResponseStatus.YES ? 1 : 0;

        if (!ObjectUtils.isEmpty(request.getFamilyMembers())) {
            verifyFamilyMembersAccess(primaryUser, request.getFamilyMembers());

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
    @Override
    @Transactional(readOnly = true)
    public RsvpResponse getRsvpStatus(String username) {
        User primaryUser = getPrimaryUserByUsername(username);

        UserResponse primaryResponse = userResponseRepository.findByUserId(primaryUser.getId())
                .orElse(null);

        int confirmedAttendees = 0;
        if (primaryResponse != null && primaryResponse.getStatus() == ResponseStatus.YES) {
            confirmedAttendees = 1;
        }

        if (primaryUser.getFamily() != null) {
            List<User> familyMembers = userService.getFamilyMembers(primaryUser.getFamily().getId());

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

    @Override
    @Transactional(readOnly = true)
    public FamilyMembersResponse getFamilyMembers(String username) {
        User primaryUser = getPrimaryUserByUsername(username);

        UserResponse primaryResponse = userResponseRepository.findByUserId(primaryUser.getId())
                .orElse(null);

        UserDto primaryUserDto = createUserDtoFromResponse(primaryUser, primaryResponse);

        List<UserDto> familyMembersDto = new ArrayList<>();

        if (primaryUser.getFamily() != null) {
            List<User> familyMembers = userService.getFamilyMembers(primaryUser.getFamily().getId());

            for (User member : familyMembers) {
                if (Objects.equals(member.getId(), primaryUser.getId())) {
                    continue;
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

    @Override
    @Transactional
    public RsvpResponse savePrimaryGuestResponse(GuestResponse guestResponse, String username) {
        User primaryUser = getPrimaryUserByUsername(username);

        if (!Objects.equals(primaryUser.getId(), guestResponse.getUserId())) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS_TO_USER_DATA);
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
    
    @Override
    @Transactional(readOnly = true)
    public AllRsvpResponsesDto getAllRsvpResponses() {
        // Fetch all responses
        List<UserResponse> allUserResponses = userResponseRepository.findAll();
        
        // Prepare counters for summary
        int confirmedGuests = 0;
        int pendingGuests = 0;
        int declinedGuests = 0;
        
        // Map to response DTOs
        List<AllRsvpResponsesDto.RsvpEntryDto> responseDtos = new ArrayList<>();
        
        for (UserResponse response : allUserResponses) {
            User user = response.getUser();
            
            // Count by status
            if (response.getStatus() == ResponseStatus.YES) {
                confirmedGuests++;
            } else if (response.getStatus() == ResponseStatus.MAYBE) {
                pendingGuests++;
            } else if (response.getStatus() == ResponseStatus.NO) {
                declinedGuests++;
            }
            
            // Get family name if user belongs to a family
            String familyName = user.getFamily() != null ? user.getFamily().getName() : "No Family";
            
            // Create the DTO
            AllRsvpResponsesDto.RsvpEntryDto entryDto = AllRsvpResponsesDto.RsvpEntryDto.builder()
                    .userId(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .status(response.getStatus().toString())
                    .isChild(user.isChild())
                    .dietaryNotes(response.getDietaryNotes())
                    .additionalNotes(response.getAdditionalNotes())
                    .familyName(familyName)
                    .build();
            
            responseDtos.add(entryDto);
        }
        
        log.info("Retrieved all RSVP responses. Total: {}, Confirmed: {}, Pending: {}, Declined: {}",
                allUserResponses.size(), confirmedGuests, pendingGuests, declinedGuests);
        
        // Build the response DTO
        return AllRsvpResponsesDto.builder()
                .responses(responseDtos)
                .totalGuests(allUserResponses.size())
                .confirmedGuests(confirmedGuests)
                .pendingGuests(pendingGuests)
                .declinedGuests(declinedGuests)
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

    private User getPrimaryUserByUsername(String username) {
        String[] names = username.split(" ");
        if (names.length != 2) {
            throw new IllegalArgumentException(INVALID_USERNAME_FORMAT);
        }

        return userService.getByFirstNameAndLastName(names[0], names[1]);
    }

    /**
     * Update a user's response status
     */
    private void updateUserResponse(GuestResponse guestResponse) {
        User user = userService.getUserById(guestResponse.getUserId());

        UserResponse response = userResponseRepository.findByUserId(user.getId())
                .orElse(UserResponse.builder()
                        .user(user)
                        .build());

        response.setStatus(guestResponse.getStatus());
        response.setDietaryNotes(guestResponse.getDietaryNotes());
        response.setAdditionalNotes(guestResponse.getAdditionalNotes());

        userResponseRepository.save(response);
        log.debug("Updated response for user Id: {}, status: {}", user.getId(), response.getStatus());
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
     * Verify that the authenticated user has access to update the specified user
     */
    private void verifyUserAccess(User authenticatedUser, Long requestUserId) {
        if (!Objects.equals(authenticatedUser.getId(), requestUserId)) {
            throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS_TO_USER_DATA);
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
        Set<Long> familyMemberIds = userService.getFamilyMembers(family.getId())
                .stream()
                .map(User::getId)
                .collect(Collectors.toSet());

        for (GuestResponse member : familyMembers) {
            if (!familyMemberIds.contains(member.getUserId())) {
                throw new UnauthorizedAccessException("Unauthorized access to family member data");
            }
        }
    }

}