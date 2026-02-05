package com.ishwor.journalapi.mapper;

import com.ishwor.journalapi.dto.UserResponse;
import com.ishwor.journalapi.entity.UserEntity;

public class UserMapper {
    public static UserResponse toResponse(UserEntity user){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
