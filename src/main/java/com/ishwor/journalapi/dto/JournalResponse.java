package com.ishwor.journalapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(description = "Response containing journal entry details")
public class JournalResponse {
    @Schema(description = "Unique identifier of the journal entry", example = "1")
    private Long id;

    @Schema(description = "Title of the journal entry", example = "My Amazing Day")
    private String title;

    @Schema(description = "Content of the journal entry", example = "Today was a great day because...")
    private String content;

    @Schema(description = "Timestamp when the journal was created", example = "2026-02-05T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the journal was last updated", example = "2026-02-05T15:45:00")
    private LocalDateTime updatedAt;
}
