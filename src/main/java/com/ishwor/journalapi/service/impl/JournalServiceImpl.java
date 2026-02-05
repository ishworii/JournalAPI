package com.ishwor.journalapi.service.impl;


import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.entity.JournalEntity;
import com.ishwor.journalapi.exception.JournalNotFoundException;
import com.ishwor.journalapi.repository.JournalRepository;
import com.ishwor.journalapi.service.JournalService;
import com.ishwor.journalapi.mapper.JournalMapper;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository repository;
    private final CurrentUserService currentUserService;
    private final JournalRepository journalRepository;
    private final SecurityExpressionHandler securityExpressionHandler;

    public JournalServiceImpl(JournalRepository journalRepository, CurrentUserService currentUserService, SecurityExpressionHandler securityExpressionHandler){
        this.repository = journalRepository;
        this.currentUserService = currentUserService;
        this.journalRepository = journalRepository;
        this.securityExpressionHandler = securityExpressionHandler;
    }

    @Override
    public List<JournalResponse> getAll() {
        if(currentUserService.isAdmin()){
            return repository.findAll().stream().map(JournalMapper::toResponse).toList();
        }
        Long userId = currentUserService.getCurrentUser().getId();
        return journalRepository.findAllOwnerById(userId)
                .stream()
                .map(JournalMapper::toResponse)
                .toList();

    }

    @Override
    public JournalResponse getById(Long id) {
        if(currentUserService.isAdmin()){
            JournalEntity entity = repository.findById(id)
                    .orElseThrow(() -> new JournalNotFoundException(id));
            return JournalMapper.toResponse(entity);
        }
        Long userId = currentUserService.getCurrentUser().getId();
        var entity = repository.findByIdAndOwnerId(id,userId)
                .orElseThrow(() -> new JournalNotFoundException(id));
        return JournalMapper.toResponse(entity);
    }

    @Override
    public JournalResponse create(JournalRequest request) {
        var user = currentUserService.getCurrentUser();

        JournalEntity journalEntity = JournalMapper.toEntity(request);
        journalEntity.setOwner(user);

        JournalEntity saved = repository.save(journalEntity);
        return JournalMapper.toResponse(saved);
    }

    @Override
    public JournalResponse update(Long id, JournalRequest request) {
        if (currentUserService.isAdmin()){
            JournalEntity existing = repository.findById(id)
                    .orElseThrow(() -> new JournalNotFoundException(id));
            existing.setTitle(request.getTitle());
            existing.setContent(request.getContent());
            JournalEntity saved = repository.save(existing);
            return JournalMapper.toResponse(saved);
        }
        Long userId = currentUserService.getCurrentUser().getId();
        JournalEntity existing = repository.findByIdAndOwnerId(id,userId)
                .orElseThrow(() -> new JournalNotFoundException(id));
        existing.setTitle(request.getTitle());
        existing.setContent(request.getContent());
        JournalEntity saved = repository.save(existing);
        return JournalMapper.toResponse(saved);

    }

    @Override
    public void delete(Long id) {
        if(currentUserService.isAdmin()){
            if(!repository.existsById(id)){
                throw new JournalNotFoundException(id);
            }
            repository.deleteById(id);
        }
        Long userId = currentUserService.getCurrentUser().getId();
        if(!repository.existsByIdAndOwnerId(id,userId)){
            throw new JournalNotFoundException(id);
        }
        repository.deleteByIdAndOwnerId(id,userId);

    }
}
