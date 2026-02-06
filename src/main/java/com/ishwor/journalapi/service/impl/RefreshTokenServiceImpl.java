package com.ishwor.journalapi.service.impl;

import com.ishwor.journalapi.entity.RefreshTokenEntity;
import com.ishwor.journalapi.exception.RefreshTokenException;
import com.ishwor.journalapi.repository.RefreshTokenEntityRepository;
import com.ishwor.journalapi.repository.UserRepository;
import com.ishwor.journalapi.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

   private final RefreshTokenEntityRepository refreshTokenEntityRepository;
   private final UserRepository userRepository;

   public RefreshTokenServiceImpl(RefreshTokenEntityRepository refreshTokenEntityRepository, UserRepository userRepository){
       this.refreshTokenEntityRepository = refreshTokenEntityRepository;
       this.userRepository = userRepository;
   }

    @Override
    @Transactional
    public RefreshTokenEntity createRefreshToken(String email) {
       // Find the user
       var user = userRepository.findByEmail(email)
               .orElseThrow(() -> new RefreshTokenException("User not found"));

       // Delete any existing refresh token for this user (because of @OneToOne relationship)
       // This prevents database constraint violations when user logs in multiple times
       refreshTokenEntityRepository.deleteByUser(user);

       // Flush to ensure delete is committed before insert
       refreshTokenEntityRepository.flush();

       // Create new refresh token
       RefreshTokenEntity refreshToken = new RefreshTokenEntity();
       refreshToken.setUser(user);
       refreshToken.setExpiryDate(Instant.now().plusSeconds(86400)); // 24 hours
       refreshToken.setToken(UUID.randomUUID().toString());

       return refreshTokenEntityRepository.save(refreshToken);
    }

    @Override
    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity refreshToken) {
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0){
            refreshTokenEntityRepository.delete(refreshToken);
            throw new RefreshTokenException("Refresh token expired. Please login again.");
        }
        return refreshToken;
    }

    @Override
    public Optional<RefreshTokenEntity> findByToken(String token){
       return refreshTokenEntityRepository.findByToken(token);
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenEntityRepository.deleteByUserId(userId);
    }
}
