package com.ishwor.helloworld.service;

import com.ishwor.helloworld.dto.AuthResponse;
import com.ishwor.helloworld.dto.LoginRequest;
import com.ishwor.helloworld.dto.RegisterRequest;

public interface AuthService {
    public AuthResponse register(RegisterRequest request);
    public AuthResponse login(LoginRequest request);
}
