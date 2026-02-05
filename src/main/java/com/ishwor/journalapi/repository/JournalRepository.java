package com.ishwor.journalapi.repository;

import com.ishwor.journalapi.entity.JournalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface JournalRepository extends JpaRepository<JournalEntity, Long> {
    List<JournalEntity> findAllOwnerById(Long ownerId);
    Optional<JournalEntity> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    void deleteByIdAndOwnerId(Long id, Long ownerId);
}