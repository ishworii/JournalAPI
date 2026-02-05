package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.UserResponse;
import com.ishwor.journalapi.mapper.UserMapper;
import com.ishwor.journalapi.service.impl.CurrentUserService;
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
