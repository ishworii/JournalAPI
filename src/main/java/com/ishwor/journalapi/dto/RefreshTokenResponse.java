package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing new access token and refresh token after token refresh")
public class RefreshTokenResponse {
    @Schema(description = "New JWT access token valid for 15 minutes",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "New refresh token (token rotation - old token is now invalid)",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}
