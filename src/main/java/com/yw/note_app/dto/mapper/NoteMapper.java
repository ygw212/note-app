package com.yw.note_app.dto.mapper;

import com.yw.note_app.dto.response.NoteResponse;
import com.yw.note_app.model.Note;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class NoteMapper {

    public NoteResponse toNoteResponse(Note note) {
        NoteResponse response = new NoteResponse();
        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setOwnerUsername(note.getOwner().getUsername());
        response.setSharedWithUsernames(
                note.getSharedWith().stream()
                        .map(user -> user.getUsername())
                        .collect(Collectors.toSet())
        );
        response.setCreatedAt(note.getCreatedAt());
        response.setUpdatedAt(note.getUpdatedAt());
        return response;
    }
}
