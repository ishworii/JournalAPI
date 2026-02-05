package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.AuthResponse;
import com.ishwor.journalapi.dto.LoginRequest;
import com.ishwor.journalapi.dto.RegisterRequest;
import com.ishwor.journalapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "APIs for user authentication and registration. These endpoints do not require authentication.")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
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
}
