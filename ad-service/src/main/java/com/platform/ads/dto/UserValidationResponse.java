package com.platform.ads.dto;

import lombok.Data;

@Data
public class UserValidationResponse {
    private boolean exists;
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
}
