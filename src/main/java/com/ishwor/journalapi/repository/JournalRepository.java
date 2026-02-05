package com.ishwor.journalapi.repository;

import com.ishwor.journalapi.entity.JournalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface JournalRepository extends JpaRepository<JournalEntity, Long> {
    Page<JournalEntity> findAll(Pageable pageable);
    Page<JournalEntity> findAllByOwnerId(Long ownerId, Pageable pageable);
    Optional<JournalEntity> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByIdAndOwnerId(Long id, Long ownerId);
    void deleteByIdAndOwnerId(Long id, Long ownerId);
}