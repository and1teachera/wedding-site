package com.zlatenov.wedding_backend.service;

import com.zlatenov.wedding_backend.dto.SingleUserAccommodationResponsesDto;
import com.zlatenov.wedding_backend.exception.ResourceNotFoundException;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationRequest;
import com.zlatenov.wedding_backend.model.SingleUserAccommodationStatus;
import com.zlatenov.wedding_backend.model.User;
import com.zlatenov.wedding_backend.repository.SingleUserAccommodationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccommodationServiceImpl implements AccommodationService {

    private final SingleUserAccommodationRepository singleUserAccommodationRepository;

    @Override
    @Transactional(readOnly = true)
    public SingleUserAccommodationResponsesDto getAllSingleUserRequests() {
        // Get all requests ordered by date (newest first)
        List<SingleUserAccommodationRequest> allRequests = singleUserAccommodationRepository.findAllByOrderByRequestDateDesc();
        Map<User, List<SingleUserAccommodationRequest>> usersToAccommodationRequests = allRequests.stream()
                .collect(Collectors.groupingBy(SingleUserAccommodationRequest::getUser));

        Map<Boolean, List<Map.Entry<User, List<SingleUserAccommodationRequest>>>> userEntriesWithMultipleRequests = usersToAccommodationRequests
                .entrySet().stream()
                .collect(Collectors.partitioningBy(userListEntry -> userListEntry.getValue().size() > 1));

        List<User> usersWithMultipleRequests = userEntriesWithMultipleRequests.get(true).stream()
                        .map(Map.Entry::getKey)
                        .toList();

        List<SingleUserAccommodationRequest> activeUserRequests = usersWithMultipleRequests.stream()
                .map(user -> singleUserAccommodationRepository.findLatestByUserId(user.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        activeUserRequests.addAll(userEntriesWithMultipleRequests.get(false).stream()
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .toList());

        // Count statistics
        int totalRequests = activeUserRequests.size();
        long pendingRequests = activeUserRequests.stream()
                .filter(singleUserAccommodationRequest -> singleUserAccommodationRequest.getStatus() == SingleUserAccommodationStatus.PENDING)
                .count();
        long processedRequests = activeUserRequests.stream()
                .filter(SingleUserAccommodationRequest::isProcessed)
                .count();
        long cancelledRequests = activeUserRequests.stream()
                .filter(singleUserAccommodationRequest -> singleUserAccommodationRequest.getStatus() == SingleUserAccommodationStatus.CANCELLED)
                .count();
        
        // Map to DTOs
        List<SingleUserAccommodationResponsesDto.SingleUserRequestDto> requestDtos = activeUserRequests.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        // Build the response
        return SingleUserAccommodationResponsesDto.builder()
                .requests(requestDtos)
                .totalRequests(totalRequests)
                .pendingRequests((int) pendingRequests)
                .processedRequests((int) processedRequests)
                .cancelledRequests((int) cancelledRequests)
                .build();
    }
    
    @Override
    @Transactional
    public boolean approveSingleUserRequest(Long requestId) {
        SingleUserAccommodationRequest request = findRequestById(requestId);
        
        if (request.getStatus() != SingleUserAccommodationStatus.PENDING) {
            log.warn("Attempted to approve request with ID {} that is not in PENDING state", requestId);
            return false;
        }
        
        // Create a new request record with APPROVED status
        SingleUserAccommodationRequest approvedRequest = SingleUserAccommodationRequest.builder()
                .user(request.getUser())
                .status(SingleUserAccommodationStatus.APPROVED)
                .notes("Approved: " + (request.getNotes() != null ? request.getNotes() : ""))
                .processed(true)
                .processedDate(LocalDateTime.now())
                .group(request.getGroup()) // Preserve group assignment if any
                .build();
        
        singleUserAccommodationRepository.save(approvedRequest);
        log.info("Single user accommodation request with ID {} has been approved", requestId);
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean rejectSingleUserRequest(Long requestId) {
        SingleUserAccommodationRequest request = findRequestById(requestId);
        
        if (request.getStatus() != SingleUserAccommodationStatus.PENDING) {
            log.warn("Attempted to reject request with ID {} that is not in PENDING state", requestId);
            return false;
        }
        
        // Create a new request record with REJECTED status
        SingleUserAccommodationRequest rejectedRequest = SingleUserAccommodationRequest.builder()
                .user(request.getUser())
                .status(SingleUserAccommodationStatus.REJECTED)
                .notes("Rejected: " + (request.getNotes() != null ? request.getNotes() : ""))
                .processed(true)
                .processedDate(LocalDateTime.now())
                .group(request.getGroup()) // Preserve group assignment if any
                .build();
        
        singleUserAccommodationRepository.save(rejectedRequest);
        log.info("Single user accommodation request with ID {} has been rejected", requestId);
        
        return true;
    }
    
    private SingleUserAccommodationRequest findRequestById(Long requestId) {
        return singleUserAccommodationRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Single user accommodation request not found with ID: " + requestId));
    }
    
    private SingleUserAccommodationResponsesDto.SingleUserRequestDto mapToDto(SingleUserAccommodationRequest request) {
        return SingleUserAccommodationResponsesDto.SingleUserRequestDto.builder()
                .requestId(request.getId())
                .userId(request.getUser().getId())
                .userName(request.getUser().getFirstName() + " " + request.getUser().getLastName())
                .requestDate(request.getRequestDate())
                .status(request.getStatus().name())
                .notes(request.getNotes())
                .processed(request.isProcessed())
                .processedDate(request.getProcessedDate())
                .build();
    }
}