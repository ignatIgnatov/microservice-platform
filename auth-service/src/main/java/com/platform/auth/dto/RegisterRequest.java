package com.platform.auth.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String telephone;
    private String password;
    private String confirmPassword;
}