package com.yw.note_app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

public class NoteRequest {
    @Schema(
            description = "Note title",
            example = "Meeting Notes",
            required = true
    )
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(
            description = "Note content",
            example = "Discussion points for the team meeting...",
            required = true
    )
    @NotBlank(message = "Content is required")
    private String content;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}