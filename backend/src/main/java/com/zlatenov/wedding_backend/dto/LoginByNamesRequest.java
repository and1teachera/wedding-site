package com.zlatenov.wedding_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Angel Zlatenov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginByNamesRequest {
    private String firstName;

    private String lastName;

    private String password;
}