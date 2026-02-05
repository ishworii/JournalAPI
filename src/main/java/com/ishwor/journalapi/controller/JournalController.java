package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.service.JournalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService){
        this.journalService = journalService;
    }

    @GetMapping
    public List<JournalResponse> getAll(){
        return journalService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalResponse createJournal(@RequestBody JournalRequest request){
       return journalService.create(request) ;
    }

    @GetMapping("/{id}")
    public JournalResponse getById(@PathVariable Long id){
        return journalService.getById(id);
    }

    @PutMapping("/{id}")
    public JournalResponse updateJournal(@PathVariable Long id, @RequestBody JournalRequest request){
        return journalService.update(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJournal(@PathVariable Long id){
        journalService.delete(id);
    }




}
