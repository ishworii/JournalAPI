package com.ishwor.helloworld.service;

import com.ishwor.helloworld.dto.JournalRequest;
import com.ishwor.helloworld.dto.JournalResponse;

import java.util.List;

public interface JournalService {
    List<JournalResponse> getAll();
    JournalResponse getById(Long id);
    JournalResponse create(JournalRequest request);
    JournalResponse update(Long id, JournalRequest request);
    void delete(Long id);
}
