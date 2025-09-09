package com.platform.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Request object for updating company profile information")
public class UpdateCompanyRequest {

    @Size(max = 255, message = "Company name must not exceed 255 characters")
    private String companyName;

    @Size(max = 255, message = "Store name must not exceed 255 characters")
    private String storeName;

    @Size(max = 20, message = "Business registration number must not exceed 20 characters")
    @Pattern(regexp = "^[0-9]{9}([0-9]{4})?$", message = "Business registration number must be 9 or 13 digits")
    private String businessRegistrationNumber;

    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(min = 7, max = 20, message = "Phone number must be between 7 and 20 characters")
    private String phone;

    @Email(message = "Invalid email format")
    private String contactEmail;

    @Schema(hidden = true)
    public boolean hasAnyUpdates() {
        return companyName != null || storeName != null ||
                businessRegistrationNumber != null || city != null ||
                address != null || phone != null ||
                contactEmail != null;
    }
}