package com.ishwor.journalapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.entity.Role;
import com.ishwor.journalapi.entity.UserEntity;
import com.ishwor.journalapi.exception.JournalNotFoundException;
import com.ishwor.journalapi.service.JournalService;
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

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class JournalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private JournalService journalService;

    @MockitoBean
    private CurrentUserService currentUserService;

    @Test
    @WithMockUser
    public void shouldGetAllJournals() throws Exception {
        JournalResponse journal1 = new JournalResponse();
        journal1.setId(1L);
        journal1.setTitle("First Journal");
        journal1.setContent("First content");

        JournalResponse journal2 = new JournalResponse();
        journal2.setId(2L);
        journal2.setTitle("Second Journal");
        journal2.setContent("Second content");

        List<JournalResponse> journals = Arrays.asList(journal1, journal2);

        Mockito.when(journalService.getAll()).thenReturn(journals);

        mockMvc.perform(get("/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("First Journal"))
                .andExpect(jsonPath("$[0].content").value("First content"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Second Journal"))
                .andExpect(jsonPath("$[1].content").value("Second content"));
    }

    @Test
    @WithMockUser
    public void shouldCreateJournal_WithValidRequest() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("New Journal");
        request.setContent("New content");

        JournalResponse response = new JournalResponse();
        response.setId(1L);
        response.setTitle("New Journal");
        response.setContent("New content");

        Mockito.when(journalService.create(Mockito.any(JournalRequest.class))).thenReturn(response);

        mockMvc.perform(post("/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New Journal"))
                .andExpect(jsonPath("$.content").value("New content"));
    }

    @Test
    @WithMockUser
    public void shouldGetJournalById() throws Exception {
        JournalResponse response = new JournalResponse();
        response.setId(1L);
        response.setTitle("Test Journal");
        response.setContent("Test content");

        Mockito.when(journalService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/journal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Journal"))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    @WithMockUser
    public void shouldUpdateJournal() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("Updated Journal");
        request.setContent("Updated content");

        JournalResponse response = new JournalResponse();
        response.setId(1L);
        response.setTitle("Updated Journal");
        response.setContent("Updated content");

        Mockito.when(journalService.update(Mockito.eq(1L), Mockito.any(JournalRequest.class))).thenReturn(response);

        mockMvc.perform(put("/journal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Journal"))
                .andExpect(jsonPath("$.content").value("Updated content"));
    }

    @Test
    @WithMockUser
    public void shouldDeleteJournal() throws Exception {
        Mockito.doNothing().when(journalService).delete(1L);

        mockMvc.perform(delete("/journal/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(journalService, Mockito.times(1)).delete(1L);
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated_GetAll() throws Exception {
        mockMvc.perform(get("/journal"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated_Create() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("Unauthorized Journal");
        request.setContent("Should not be created");

        mockMvc.perform(post("/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated_GetById() throws Exception {
        mockMvc.perform(get("/journal/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated_Update() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("Unauthorized Update");
        request.setContent("Should not update");

        mockMvc.perform(put("/journal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldReturnUnauthorized_WhenNotAuthenticated_Delete() throws Exception {
        mockMvc.perform(delete("/journal/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void shouldReturnEmptyList_WhenNoJournals() throws Exception {
        Mockito.when(journalService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // OWNERSHIP TESTS

    @Test
    @WithMockUser(username = "user1@example.com")
    public void shouldOnlyReturnOwnJournals_ForRegularUser() throws Exception {
        JournalResponse ownJournal = new JournalResponse();
        ownJournal.setId(1L);
        ownJournal.setTitle("User1's Journal");
        ownJournal.setContent("User1's content");

        List<JournalResponse> userJournals = Arrays.asList(ownJournal);

        Mockito.when(journalService.getAll()).thenReturn(userJournals);

        mockMvc.perform(get("/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("User1's Journal"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void shouldReturnAllJournals_ForAdmin() throws Exception {
        JournalResponse journal1 = new JournalResponse();
        journal1.setId(1L);
        journal1.setTitle("User1's Journal");
        journal1.setContent("User1's content");

        JournalResponse journal2 = new JournalResponse();
        journal2.setId(2L);
        journal2.setTitle("User2's Journal");
        journal2.setContent("User2's content");

        List<JournalResponse> allJournals = Arrays.asList(journal1, journal2);

        Mockito.when(journalService.getAll()).thenReturn(allJournals);

        mockMvc.perform(get("/journal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("User1's Journal"))
                .andExpect(jsonPath("$[1].title").value("User2's Journal"));
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void shouldNotGetJournal_WhenNotOwner() throws Exception {
        Mockito.when(journalService.getById(999L))
                .thenThrow(new JournalNotFoundException(999L));

        mockMvc.perform(get("/journal/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void shouldGetAnyJournal_WhenAdmin() throws Exception {
        JournalResponse journal = new JournalResponse();
        journal.setId(1L);
        journal.setTitle("Someone's Journal");
        journal.setContent("Some content");

        Mockito.when(journalService.getById(1L)).thenReturn(journal);

        mockMvc.perform(get("/journal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Someone's Journal"));
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void shouldNotUpdateJournal_WhenNotOwner() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("Attempted Update");
        request.setContent("Should not work");

        Mockito.when(journalService.update(Mockito.eq(999L), Mockito.any(JournalRequest.class)))
                .thenThrow(new JournalNotFoundException(999L));

        mockMvc.perform(put("/journal/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void shouldUpdateAnyJournal_WhenAdmin() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("Admin Updated");
        request.setContent("Admin can update");

        JournalResponse response = new JournalResponse();
        response.setId(1L);
        response.setTitle("Admin Updated");
        response.setContent("Admin can update");

        Mockito.when(journalService.update(Mockito.eq(1L), Mockito.any(JournalRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/journal/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Admin Updated"));
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void shouldNotDeleteJournal_WhenNotOwner() throws Exception {
        Mockito.doThrow(new JournalNotFoundException(999L))
                .when(journalService).delete(999L);

        mockMvc.perform(delete("/journal/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void shouldDeleteAnyJournal_WhenAdmin() throws Exception {
        Mockito.doNothing().when(journalService).delete(1L);

        mockMvc.perform(delete("/journal/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(journalService, Mockito.times(1)).delete(1L);
    }

    @Test
    @WithMockUser(username = "user1@example.com")
    public void shouldCreateJournal_WithCurrentUserAsOwner() throws Exception {
        JournalRequest request = new JournalRequest();
        request.setTitle("My New Journal");
        request.setContent("My content");

        JournalResponse response = new JournalResponse();
        response.setId(1L);
        response.setTitle("My New Journal");
        response.setContent("My content");

        Mockito.when(journalService.create(Mockito.any(JournalRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/journal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("My New Journal"));
    }
}
