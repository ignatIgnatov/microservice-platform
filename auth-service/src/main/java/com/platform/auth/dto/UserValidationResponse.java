package com.platform.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserValidationResponse {
    private boolean exists;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
}