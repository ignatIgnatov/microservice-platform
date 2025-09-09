package com.platform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfoResponse {
    private String id;
    private String companyName;
    private String storeName;
    private String businessRegistrationNumber;
    private String city;
    private String address;
    private String email;
    private String phone;
    private List<String> roles;
    private String userType;
}
