package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Angel Zlatenov
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMembersResponse {
    private UserDto primaryUser;
    private List<UserDto> familyMembers;

}

