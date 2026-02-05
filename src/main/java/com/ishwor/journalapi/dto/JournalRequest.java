package com.ishwor.journalapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JournalRequest {

    @NotBlank(message = "title is required.")
    @Size(min = 1,max=200,message = "title must be 1 to 200 chars")
    private String title;

    @NotBlank(message="content is required")
    @Size(max=5000,message = "content must be <=5000 chars")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
