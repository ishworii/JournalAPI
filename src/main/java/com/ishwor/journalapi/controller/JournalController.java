package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.service.JournalService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/journal")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService){
        this.journalService = journalService;
    }

    @GetMapping
    public Page<JournalResponse> getAll(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return journalService.getAll(pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalResponse createJournal(@Valid @RequestBody JournalRequest request){
       return journalService.create(request) ;
    }

    @GetMapping("/{id}")
    public JournalResponse getById(@PathVariable Long id){
        return journalService.getById(id);
    }

    @PutMapping("/{id}")
    public JournalResponse updateJournal(@PathVariable Long id, @Valid @RequestBody JournalRequest request){
        return journalService.update(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJournal(@PathVariable Long id){
        journalService.delete(id);
    }




}
