package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing JWT authentication token")
public class AuthResponse {

    @Schema(description = "JWT token valid for 15 minutes. Use this token in the Authorization header as 'Bearer {token}' for authenticated requests.",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String token;

    public AuthResponse(String token){
        this.token = token;
    }

    public String getToken(){
        return this.token;
    }
}
