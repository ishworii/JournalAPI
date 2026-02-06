package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Request payload for user registration")
public class RegisterRequest {

    @Schema(description = "User's email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "email is required")
    @Email(message="email must be valid")
    private String email;

    @Schema(description = "User's password (8-72 characters)", example = "SecurePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password is required")
    @Size(min=8,max = 72, message = "password should be 8-72 chars")
    private String password;

    @Schema(description = "User role (defaults to USER if not specified)", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role;
}
