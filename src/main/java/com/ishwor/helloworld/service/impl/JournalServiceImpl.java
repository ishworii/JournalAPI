package com.ishwor.helloworld.service.impl;


import com.ishwor.helloworld.dto.JournalRequest;
import com.ishwor.helloworld.dto.JournalResponse;
import com.ishwor.helloworld.entity.JournalEntity;
import com.ishwor.helloworld.exception.JournalNotFoundException;
import com.ishwor.helloworld.repository.JournalRepository;
import com.ishwor.helloworld.service.JournalService;
import com.ishwor.helloworld.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository repository;

    public JournalServiceImpl(JournalRepository journalRepository){
        this.repository = journalRepository;
    }

    @Override
    public List<JournalResponse> getAll() {
        return repository.findAll().stream().map(Mapper::toResponse).toList();
    }

    @Override
    public JournalResponse getById(Long id) {
        JournalEntity entity = repository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException(id));
        return Mapper.toResponse(entity);
    }

    @Override
    public JournalResponse create(JournalRequest request) {
        JournalEntity journalEntity = Mapper.toEntity(request);
        JournalEntity saved = repository.save(journalEntity);
        return Mapper.toResponse(saved);
    }

    @Override
    public JournalResponse update(Long id, JournalRequest request) {
        JournalEntity existing = repository.findById(id)
                .orElseThrow(() -> new JournalNotFoundException(id));
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        JournalEntity saved = repository.save(existing);
        return Mapper.toResponse(saved);
    }

    @Override
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new JournalNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
