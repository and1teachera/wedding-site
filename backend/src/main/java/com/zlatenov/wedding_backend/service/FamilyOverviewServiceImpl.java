package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.FamilyOverviewDto;
import com.zlatenov.wedding_backend.model.BookingStatus;
import com.zlatenov.wedding_backend.model.Family;
import com.zlatenov.wedding_backend.model.ResponseStatus;
import com.zlatenov.wedding_backend.model.RoomBooking;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.model.UserResponse;
import com.zlatenov.wedding_backend.repository.FamilyRepository;
import com.zlatenov.wedding_backend.repository.RoomBookingRepository;
import com.zlatenov.wedding_backend.repository.SingleUserAccommodationRepository;
import com.zlatenov.wedding_backend.repository.UserRepository;
import com.zlatenov.wedding_backend.repository.UserResponseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service for providing family and guest overview functionalities
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FamilyOverviewServiceImpl implements FamilyOverviewService {

    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;
    private final UserResponseRepository userResponseRepository;
    private final RoomBookingRepository roomBookingRepository;
    private final SingleUserAccommodationRepository singleUserAccommodationRepository;

    /**
     * Get an overview of all families, their members, and single users (users without a family)
     * @return Complete overview of all guests
     */
    @Transactional(readOnly = true)
    public FamilyOverviewDto getFamiliesOverview() {
        // Fetch all families
        List<Family> allFamilies = familyRepository.findAll();
        
        // Get all users with families
        List<User> usersWithFamilies = userRepository.findByFamilyIsNotNull();
        
        // Get all users without families (single users)
        List<User> singleUsers = userRepository.findByFamilyIsNull();
        
        // Get all user responses
        List<UserResponse> allUserResponses = userResponseRepository.findAll();
        Map<Long, UserResponse> responsesByUserId = allUserResponses.stream()
                .collect(Collectors.toMap(response -> response.getUser().getId(), Function.identity()));
        
        // Get all room bookings
        List<RoomBooking> confirmedRoomBookings = roomBookingRepository.findByStatus(BookingStatus.CONFIRMED);
        Map<Long, RoomBooking> bookingsByFamilyId = confirmedRoomBookings.stream()
                .filter(booking -> booking.getFamily() != null)
                .collect(Collectors.toMap(
                        booking -> booking.getFamily().getId(),
                        Function.identity(),
                        (existing, replacement) -> replacement  // Keep the latest booking in case of duplicates
                ));
        
        // Process families data
        List<FamilyOverviewDto.FamilyDto> familyDtos = new ArrayList<>();
        int totalConfirmedGuests = 0;
        
        for (Family family : allFamilies) {
            List<User> familyMembers = usersWithFamilies.stream()
                    .filter(user -> user.getFamily() != null && 
                           user.getFamily().getId().equals(family.getId()))
                    .collect(Collectors.toList());
            
            List<FamilyOverviewDto.FamilyMemberDto> memberDtos = new ArrayList<>();
            int confirmedMembers = 0;
            
            for (User member : familyMembers) {
                UserResponse response = responsesByUserId.get(member.getId());
                ResponseStatus status = response != null ? response.getStatus() : ResponseStatus.MAYBE;
                
                if (status == ResponseStatus.YES) {
                    confirmedMembers++;
                    totalConfirmedGuests++;
                }
                
                FamilyOverviewDto.FamilyMemberDto memberDto = FamilyOverviewDto.FamilyMemberDto.builder()
                        .id(member.getId())
                        .firstName(member.getFirstName())
                        .lastName(member.getLastName())
                        .isChild(member.isChild())
                        .rsvpStatus(status.name())
                        .dietaryNotes(response != null ? response.getDietaryNotes() : null)
                        .additionalNotes(response != null ? response.getAdditionalNotes() : null)
                        .build();
                
                memberDtos.add(memberDto);
            }
            
            boolean hasRoomBooked = bookingsByFamilyId.containsKey(family.getId());
            Integer roomNumber = null;
            
            if (hasRoomBooked) {
                RoomBooking booking = bookingsByFamilyId.get(family.getId());
                roomNumber = booking.getRoom().getRoomNumber();
            }
            
            FamilyOverviewDto.FamilyDto familyDto = FamilyOverviewDto.FamilyDto.builder()
                    .id(family.getId())
                    .name(family.getName())
                    .members(memberDtos)
                    .hasRoomBooked(hasRoomBooked)
                    .roomNumber(roomNumber)
                    .totalMembers(familyMembers.size())
                    .confirmedMembers(confirmedMembers)
                    .build();
            
            familyDtos.add(familyDto);
        }
        
        // Process single users
        List<FamilyOverviewDto.SingleUserDto> singleUserDtos = new ArrayList<>();
        
        for (User user : singleUsers) {
            UserResponse response = responsesByUserId.get(user.getId());
            ResponseStatus status = response != null ? response.getStatus() : ResponseStatus.MAYBE;
            
            if (status == ResponseStatus.YES) {
                totalConfirmedGuests++;
            }
            
            // Check for accommodation requests
            Optional<SingleUserAccommodationRequest> latestRequest = 
                    singleUserAccommodationRepository.findLatestByUserId(user.getId());
            
            boolean hasAccommodationRequest = latestRequest.isPresent();
            String accommodationStatus = latestRequest
                    .map(request -> request.getStatus().name())
                    .orElse(null);
            
            FamilyOverviewDto.SingleUserDto singleUserDto = FamilyOverviewDto.SingleUserDto.builder()
                    .id(user.getId())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .email(user.getEmail())
                    .rsvpStatus(status.name())
                    .hasAccommodationRequest(hasAccommodationRequest)
                    .accommodationStatus(accommodationStatus)
                    .build();
            
            singleUserDtos.add(singleUserDto);
        }
        
        // Build the final response
        return FamilyOverviewDto.builder()
                .families(familyDtos)
                .singleUsers(singleUserDtos)
                .totalFamilies(allFamilies.size())
                .totalSingleUsers(singleUsers.size())
                .totalGuests(usersWithFamilies.size() + singleUsers.size())
                .confirmedGuests(totalConfirmedGuests)
                .build();
    }
}