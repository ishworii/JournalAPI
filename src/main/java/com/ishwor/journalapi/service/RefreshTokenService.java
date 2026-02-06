package com.ishwor.journalapi.service;

import com.ishwor.journalapi.entity.RefreshTokenEntity;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshTokenEntity createRefreshToken(String username);
    RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshToken);
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUserId(Long userId);
}
