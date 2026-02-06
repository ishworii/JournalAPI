package com.ishwor.journalapi.repository;

import com.ishwor.journalapi.entity.RefreshTokenEntity;
import com.ishwor.journalapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user = :user")
    void deleteByUser(UserEntity user);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user.id = :userId")
    void deleteByUserId(Long userId);
}