package com.ishwor.helloworld.mapper;

import com.ishwor.helloworld.dto.JournalRequest;
import com.ishwor.helloworld.dto.JournalResponse;
import com.ishwor.helloworld.entity.JournalEntity;

public class Mapper {

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
        return response;
    }
}
