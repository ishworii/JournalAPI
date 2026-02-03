package com.ishwor.helloworld.service;

import com.ishwor.helloworld.entity.Role;

public interface JwtService {
    public String generateToken(Long userId, String email, Role role);
    public String extractSubject(String token);
    public String extractRole(String token);

}
