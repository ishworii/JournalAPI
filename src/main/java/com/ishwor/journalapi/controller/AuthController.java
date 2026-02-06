package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.*;
import com.ishwor.journalapi.entity.RefreshTokenEntity;
import com.ishwor.journalapi.exception.RefreshTokenException;
import com.ishwor.journalapi.service.AuthService;
import com.ishwor.journalapi.service.JwtService;
import com.ishwor.journalapi.service.RefreshTokenService;
import com.ishwor.journalapi.service.impl.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration. These endpoints do not require authentication.")
public class AuthController {
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService, CurrentUserService currentUserService){
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.currentUserService = currentUserService;
    }
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account. By default, users are assigned the USER role. Returns a JWT token that can be used for authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists", content = @Content)
    })
    @PostMapping("/register")
    public AuthResponse register(
            @Parameter(description = "User registration data", required = true)
            @Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @Operation(
            summary = "Login",
            description = "Authenticates a user with email and password. Returns a JWT token valid for 15 minutes."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public AuthResponse login(
            @Parameter(description = "User login credentials", required = true)
            @Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @Operation(
            summary = "Refresh access token",
            description = "Issues a new access token using a valid refresh token. Also returns a NEW refresh token (token rotation). The old refresh token is invalidated after use."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token", content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @Parameter(description = "Refresh token to exchange for new access token", required = true)
            @Valid @RequestBody RefreshTokenRequest request){
       return refreshTokenService.findByToken(request.getRefreshToken())
               .map(refreshTokenService::verifyExpiration)  // Check if expired
               .map(RefreshTokenEntity::getUser)           // Get the user
               .map(user->{
                   // Generate new access token (15 minutes)
                   String newAccessToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole());

                   // TOKEN ROTATION: Create a brand NEW refresh token
                   // This invalidates the old refresh token
                   RefreshTokenEntity newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                   return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken()));
               })
               .orElseThrow(() -> new RefreshTokenException("Invalid refresh token. Please login again."));
    }

    @Operation(
            summary = "Logout",
            description = "Invalidates the user's refresh token. The user will need to login again after logout. Access tokens remain valid until they expire (15 minutes)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> logout() {
        // Get the currently authenticated user
        Long userId = currentUserService.getCurrentUser().getId();

        // Delete their refresh token from the database
        refreshTokenService.deleteByUserId(userId);

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully",
                "note", "Your refresh token has been revoked. Access token will remain valid until expiration."
        ));
    }
}
