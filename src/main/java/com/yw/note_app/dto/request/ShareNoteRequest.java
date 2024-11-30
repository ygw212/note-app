package com.yw.note_app.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ShareNoteRequest {
    @NotBlank(message = "Username is required")
    private String username;

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
