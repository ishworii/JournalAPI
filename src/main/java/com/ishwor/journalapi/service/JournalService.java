package com.ishwor.journalapi.service;

import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface JournalService {
    Page<JournalResponse> getAll(Pageable pageable);
    JournalResponse getById(Long id);
    JournalResponse create(JournalRequest request);
    JournalResponse update(Long id, JournalRequest request);
    void delete(Long id);
}
