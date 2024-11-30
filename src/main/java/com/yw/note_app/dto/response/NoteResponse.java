package com.yw.note_app.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class NoteResponse {
    private Long id;
    private String title;
    private String content;
    private String ownerUsername;
    private Set<String> sharedWithUsernames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public Set<String> getSharedWithUsernames() {
        return sharedWithUsernames;
    }

    public void setSharedWithUsernames(Set<String> sharedWithUsernames) {
        this.sharedWithUsernames = sharedWithUsernames;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}