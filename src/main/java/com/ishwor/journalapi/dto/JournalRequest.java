package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Request payload for creating or updating a journal entry")
public class JournalRequest {

    @Schema(description = "Title of the journal entry", example = "My Amazing Day", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "title is required.")
    @Size(min = 1,max=200,message = "title must be 1 to 200 chars")
    private String title;

    @Schema(description = "Content of the journal entry", example = "Today was a great day because...", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message="content is required")
    @Size(max=5000,message = "content must be <=5000 chars")
    private String content;
}
