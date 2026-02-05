package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.entity.Role;
import com.ishwor.journalapi.entity.UserEntity;
import com.ishwor.journalapi.service.impl.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    @WithMockUser
    public void shouldReturnUser_WhenAuthenticated() throws Exception{
        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.USER);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        mockMvc.perform(get("/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    public void shouldReturnAdminUser_WhenAuthenticatedAsAdmin() throws Exception{
        UserEntity mockAdminUser = new UserEntity();
        mockAdminUser.setId(2L);
        mockAdminUser.setEmail("admin@example.com");
        mockAdminUser.setRole(Role.ADMIN);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockAdminUser);

        mockMvc.perform(get("/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated() throws Exception{
        mockMvc.perform(get("/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void shouldReturnCorrectJsonStructure() throws Exception{
        UserEntity mockUser = new UserEntity();
        mockUser.setId(3L);
        mockUser.setEmail("structure@test.com");
        mockUser.setRole(Role.USER);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        mockMvc.perform(get("/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.role").exists())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.email").isString())
                .andExpect(jsonPath("$.role").isString());
    }

    @Test
    @WithMockUser
    public void shouldHandleDifferentEmailFormats() throws Exception{
        UserEntity mockUser = new UserEntity();
        mockUser.setId(4L);
        mockUser.setEmail("user.name+tag@subdomain.example.com");
        mockUser.setRole(Role.USER);

        Mockito.when(currentUserService.getCurrentUser()).thenReturn(mockUser);

        mockMvc.perform(get("/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user.name+tag@subdomain.example.com"));
    }
}
