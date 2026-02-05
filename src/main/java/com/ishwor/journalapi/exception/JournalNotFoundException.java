package com.ishwor.journalapi.exception;

public class JournalNotFoundException extends RuntimeException{
    public JournalNotFoundException(Long id){
        super("Journal not found with id:" + id);
    }
}
