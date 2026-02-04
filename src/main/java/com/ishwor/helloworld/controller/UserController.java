package com.ishwor.helloworld.controller;


import com.ishwor.helloworld.dto.UserResponse;
import com.ishwor.helloworld.mapper.UserMapper;
import com.ishwor.helloworld.service.impl.CurrentUserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final CurrentUserService currentUserService;

    public UserController(CurrentUserService currentUserService){
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    public UserResponse me(){
        return UserMapper.toResponse(currentUserService.getCurrentUser());
    }

}
