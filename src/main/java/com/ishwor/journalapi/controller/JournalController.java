package com.ishwor.journalapi.controller;


import com.ishwor.journalapi.dto.JournalRequest;
import com.ishwor.journalapi.dto.JournalResponse;
import com.ishwor.journalapi.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/journal")
@Tag(name = "Journal Management", description = "APIs for managing personal journal entries. Regular users can only access their own journals, while admins can access all journals.")
@SecurityRequirement(name = "bearerAuth")
public class JournalController {

    private final JournalService journalService;

    public JournalController(JournalService journalService){
        this.journalService = journalService;
    }

    @Operation(
            summary = "Get all journals",
            description = "Retrieves a paginated list of journals. Regular users see only their own journals, admins see all journals. Results are sorted by creation date (newest first) by default."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved journals"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @GetMapping
    public Page<JournalResponse> getAll(
            @Parameter(description = "Pagination and sorting parameters. Default: page 0, size 10, sorted by createdAt DESC")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        return journalService.getAll(pageable);
    }

    @Operation(
            summary = "Create a new journal",
            description = "Creates a new journal entry for the authenticated user. The user is automatically set as the owner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Journal created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content)
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JournalResponse createJournal(
            @Parameter(description = "Journal data to create", required = true)
            @Valid @RequestBody JournalRequest request){
       return journalService.create(request) ;
    }

    @Operation(
            summary = "Get journal by ID",
            description = "Retrieves a specific journal by its ID. Regular users can only access their own journals, admins can access any journal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journal found and returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal not found or access denied", content = @Content)
    })
    @GetMapping("/{id}")
    public JournalResponse getById(
            @Parameter(description = "Journal ID", required = true, example = "1")
            @PathVariable Long id){
        return journalService.getById(id);
    }

    @Operation(
            summary = "Update a journal",
            description = "Updates an existing journal entry. Regular users can only update their own journals, admins can update any journal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journal updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input - validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal not found or access denied", content = @Content)
    })
    @PutMapping("/{id}")
    public JournalResponse updateJournal(
            @Parameter(description = "Journal ID to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated journal data", required = true)
            @Valid @RequestBody JournalRequest request){
        return journalService.update(id,request);
    }

    @Operation(
            summary = "Delete a journal",
            description = "Permanently deletes a journal entry. Regular users can only delete their own journals, admins can delete any journal."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Journal deleted successfully", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token", content = @Content),
            @ApiResponse(responseCode = "404", description = "Journal not found or access denied", content = @Content)
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteJournal(
            @Parameter(description = "Journal ID to delete", required = true, example = "1")
            @PathVariable Long id){
        journalService.delete(id);
    }




}
