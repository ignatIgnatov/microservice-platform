package com.platform.auth.controller;

import com.platform.auth.dto.LoginRequest;
import com.platform.auth.dto.RegisterRequest;
import com.platform.auth.dto.TokenResponse;
import com.platform.auth.dto.UserInfoResponse;
import com.platform.auth.dto.UserValidationResponse;
import com.platform.auth.exception.UserNotFoundException;
import com.platform.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register new user",
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

    @Operation(summary = "Get current user",
            description = "Returns current user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user info successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public Mono<UserInfoResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return Mono.fromSupplier(() -> {
            if (jwt == null) {
                throw new UserNotFoundException("JWT not found");
            }

            List<String> roles = getRoles(jwt);
            return UserInfoResponse.builder()
                    .email(jwt.getClaimAsString("email"))
                    .firstName(jwt.getClaimAsString("given_name"))
                    .lastName(jwt.getClaimAsString("family_name"))
                    .roles(roles)
                    .build();
        });
    }

    @Operation(summary = "Validate user by email",
            description = "Check if user exists in Keycloak")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User validation result"),
            @ApiResponse(responseCode = "500", description = "Validation failed")
    })
    @GetMapping("/validate-user")
    public Mono<ResponseEntity<UserValidationResponse>> validateUser(@RequestParam String email) {
        return authService.findUserByEmail(email)
                .map(user -> ResponseEntity.ok(UserValidationResponse.builder()
                        .exists(true)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build()))
                .onErrorReturn(UserNotFoundException.class,
                        ResponseEntity.ok(UserValidationResponse.builder()
                                .exists(false)
                                .email(email)
                                .build()));
    }

    public List<String> getRoles(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            return (List<String>) realmAccess.get("roles");
        }
        return List.of();
    }
}