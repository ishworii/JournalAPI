package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Request payload for user login")
public class LoginRequest {

    @Schema(description = "User's email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @Schema(description = "User's password", example = "SecurePassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "password is required")
    private String password;
}
