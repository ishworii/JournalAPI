package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Request payload for refreshing access token")
public class RefreshTokenRequest {
    @Schema(description = "Refresh token obtained from login or previous refresh",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "refresh token is required")
    private String refreshToken;
}
