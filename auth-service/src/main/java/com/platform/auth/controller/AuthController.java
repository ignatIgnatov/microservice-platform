package com.platform.auth.controller;

import com.platform.auth.dto.ChangePasswordRequest;
import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterCompanyRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.TokenResponse;
import com.platform.auth.dto.UpdateCompanyRequest;
import com.platform.auth.dto.UpdateUserRequest;
import com.platform.auth.exception.BusinessException;
import com.platform.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register new user",
            tags = {"User Authentication"},
            description = "Creates a new user account in Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register")
    public Mono<ResponseEntity<String>> register(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")));
    }

    @Operation(summary = "User login",
            tags = {"User Authentication, Company Authentication"},
            description = "Authenticate user with email and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "503", description = "Authentication service unavailable")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(
            @Valid @RequestBody
            @Parameter(description = "User login credentials")
            LoginRequest request) {
        return authService.authenticateUser(request)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Refresh access token",
            tags = {"User Authentication, Company Authentication"},
            description = "Get new access token using refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token"),
            @ApiResponse(responseCode = "503", description = "Authentication service unavailable")
    })
    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenResponse>> refresh(
            @RequestParam
            @Parameter(description = "Refresh token")
            String refreshToken) {
        return authService.refreshToken(refreshToken)
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "User logout",
            tags = {"User Authentication, Company Authentication"},
            description = "Invalidate refresh token and logout user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "503", description = "Authentication service unavailable")
    })
    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(
            @RequestParam
            @Parameter(description = "Refresh token")
            String refreshToken) {
        return authService.logout(refreshToken)
                .then(Mono.just(ResponseEntity.ok("Logged out successfully")));
    }

    @Operation(summary = "Get Google OAuth login URL",
            tags = {"User Authentication"},
            description = "Returns redirect URL for Google OAuth authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Google login URL generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate URL")
    })
    @GetMapping("/social/google")
    public Mono<ResponseEntity<String>> googleLogin() {
        return authService.getGoogleLoginUrl()
                .map(ResponseEntity::ok);
    }

    @Operation(summary = "Get Facebook OAuth login URL",
            tags = {"User Authentication"},
            description = "Returns redirect URL for Facebook OAuth authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facebook login URL generated"),
            @ApiResponse(responseCode = "500", description = "Failed to generate URL")
    })
    @GetMapping("/social/facebook")
    public Mono<ResponseEntity<String>> facebookLogin() {
        return authService.getFacebookLoginUrl()
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Register a new company",
            description = "Register a new company with all required business information. " +
                    "This endpoint creates a company account with company-specific attributes and assigns the 'company' role.",
            tags = {"Company Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Company registered successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - validation errors or invalid data"),
            @ApiResponse(responseCode = "409", description = "Company already exists with this email"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/register/company")
    public Mono<ResponseEntity<String>> registerCompany(
            @Parameter(
                    description = "Company registration details",
                    required = true
            )
            @Valid @RequestBody RegisterCompanyRequest request) {
        return authService.registerCompany(request)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Company registered successfully")));
    }

    @Operation(
            summary = "Get profile",
            description = "Retrieve the complete profile information and contact details. " +
                    "Requires a valid JWT token in the Authorization header.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"User Authentication, Company Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public Mono<ResponseEntity<?>> getProfile(
            @AuthenticationPrincipal Jwt jwt) {
        return authService.getProfile(jwt)
                .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Update company profile",
            description = "Update company business information and contact details. " +
                    "Only provided fields will be updated. Requires a valid JWT token in the Authorization header.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"Company Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - validation errors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/company")
    public Mono<ResponseEntity<String>> updateCompanyProfile(
            @Parameter(
                    description = "JWT Bearer token",
                    required = true,
                    example = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader("Authorization") String authHeader,
            @Parameter(
                    description = "Company profile update data. Only provided fields will be updated.",
                    required = true
            )
            @Valid @RequestBody UpdateCompanyRequest request) {

        String email = extractEmailFromJwt(authHeader);

        return authService.updateCompanyProfile(email, request)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("Company updated successfully")));
    }

//    @Operation(
//            summary = "Get company statistics",
//            description = "Retrieve company account statistics including registration date, last login, and other metrics. " +
//                    "Requires a valid JWT token in the Authorization header.",
//            security = @SecurityRequirement(name = "bearerAuth"),
//            tags = {"Company Authentication"}
//    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Company statistics retrieved successfully"),
//            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
//            @ApiResponse(responseCode = "404", description = "Company not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    @GetMapping("/statistics/company")
//    public Mono<ResponseEntity<Map<String, Object>>> getCompanyStatistics(
//            @Parameter(
//                    description = "JWT Bearer token",
//                    required = true,
//                    example = "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
//            )
//            @RequestHeader("Authorization") String authHeader) {
//
//        String email = extractEmailFromJwt(authHeader);
//
//        return authService.getCompanyStatistics(email)
//                .map(statistics -> ResponseEntity.ok(statistics))
//                .onErrorResume(UserNotFoundException.class, e -> {
//                    Map<String, Object> error = new HashMap<>();
//                    error.put("error", "COMPANY_NOT_FOUND");
//                    error.put("message", "Company not found");
//                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
//                });
//    }

    @Operation(
            summary = "Update user profile",
            description = "Update user information and contact details. " +
                    "Only provided fields will be updated. Requires a valid JWT token in the Authorization header.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"User Authentication"}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Company profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - validation errors"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid or missing token"),
            @ApiResponse(responseCode = "404", description = "Company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update/user")
    public Mono<ResponseEntity<Map<String, String>>> updateUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserRequest request) {

        String email = authentication.getName();

        return authService.updateUserProfile(email, request)
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "Profile updated successfully"))));
    }

    @Operation(
            summary = "Change password",
            description = "Change password. Requires a valid JWT token in the Authorization header.",
            security = @SecurityRequirement(name = "bearerAuth"),
            tags = {"User Authentication, Company Authentication"}
    )
    @PutMapping("/change-password")
    public Mono<ResponseEntity<Map<String, String>>> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request) {

        String email = authentication.getName();

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return Mono.just(ResponseEntity.badRequest()
                    .body(Map.of("error", "New password and confirmation do not match")));
        }

        return authService.changePassword(email, request.getCurrentPassword(), request.getNewPassword())
                .then(Mono.just(ResponseEntity.ok(Map.of("message", "Password changed successfully"))));
    }

    private String extractEmailFromJwt(String authHeader) {
        String token = authHeader.substring(7);

        try {
            String[] chunks = token.split("\\.");
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();

            String payload = new String(decoder.decode(chunks[1]));
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode jsonNode = mapper.readTree(payload);

            return jsonNode.get("email").asText();
        } catch (Exception e) {
            log.error("Failed to extract email from JWT: {}", e.getMessage());
            throw new BusinessException("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }
}