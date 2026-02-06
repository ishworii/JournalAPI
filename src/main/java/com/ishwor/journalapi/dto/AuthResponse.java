package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Response containing JWT access token and refresh token")
public class AuthResponse {

    @Schema(description = "JWT access token valid for 15 minutes. Use this in the Authorization header as 'Bearer {token}' for authenticated requests.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String accessToken;

    @Schema(description = "Refresh token valid for 24 hours. Use this to obtain a new access token when the current one expires.",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private final String refreshToken;

    public AuthResponse(String accessToken, String refreshToken){
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

}
