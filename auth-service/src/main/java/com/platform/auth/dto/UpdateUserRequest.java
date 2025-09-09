package com.platform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phone;

    /**
     * Check if the request has any updates to perform
     */
    public boolean hasAnyUpdates() {
        return (name != null && !name.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty()) ||
                (phone != null && !phone.trim().isEmpty());
    }
}