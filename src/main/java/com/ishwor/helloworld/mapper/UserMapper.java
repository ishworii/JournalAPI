package com.ishwor.helloworld.mapper;

import com.ishwor.helloworld.dto.UserResponse;
import com.ishwor.helloworld.entity.UserEntity;

public class UserMapper {
    public static UserResponse toResponse(UserEntity user){
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
