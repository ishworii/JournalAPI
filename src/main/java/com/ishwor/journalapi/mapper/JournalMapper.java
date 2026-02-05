package com.ishwor.journalapi.mapper;

import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.entity.JournalEntity;

public class JournalMapper {

    public static JournalEntity toEntity(JournalRequest request){
        JournalEntity entity = new JournalEntity();
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        return entity;
    }

    public static JournalResponse toResponse(JournalEntity entity){
        JournalResponse response = new JournalResponse();
        response.setId(entity.getId());
        response.setContent(entity.getContent());
        response.setTitle(entity.getTitle());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}
