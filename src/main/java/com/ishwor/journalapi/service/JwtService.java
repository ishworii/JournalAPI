package com.ishwor.journalapi.service;

import com.ishwor.journalapi.entity.Role;

public interface JwtService {
    public String generateToken(Long userId, String email, Role role);
    public String extractSubject(String token);
    public String extractRole(String token);

}
