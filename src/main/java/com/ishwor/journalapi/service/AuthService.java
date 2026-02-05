package com.ishwor.journalapi.service;

import com.ishwor.journalapi.dto.AuthResponse;
import com.ishwor.journalapi.dto.LoginRequest;
import com.ishwor.journalapi.dto.RegisterRequest;

public interface AuthService {
    public AuthResponse register(RegisterRequest request);
    public AuthResponse login(LoginRequest request);
}
