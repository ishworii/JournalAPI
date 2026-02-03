package com.ishwor.helloworld.repository;

import com.ishwor.helloworld.entity.JournalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JournalRepository extends JpaRepository<JournalEntity, Long> {
}