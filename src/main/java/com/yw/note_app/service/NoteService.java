package com.yw.note_app.service;

import com.yw.note_app.model.Note;
import com.yw.note_app.model.User;
import com.yw.note_app.repository.NoteRepository;
import com.yw.note_app.repository.UserRepository;
import com.yw.note_app.exception.NoteNotFoundException;
import com.yw.note_app.exception.UnauthorizedAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<Note> getAllNotes(String username, Pageable pageable) {
        User user = getUserOrThrow(username);
        return noteRepository.findByOwnerOrSharedWith(user, user, pageable);
    }

    @Transactional(readOnly = true)
    public Note getNoteById(Long noteId, String username) {
        User user = getUserOrThrow(username);
        Note note = getNoteOrThrow(noteId);

        if (!canAccessNote(note, user)) {
            throw new UnauthorizedAccessException("You don't have access to this note");
        }

        return note;
    }

    @Transactional
    public Note createNote(String title, String content, String username) {
        User owner = getUserOrThrow(username);

        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setOwner(owner);

        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long noteId, String title, String content, String username) {
        User user = getUserOrThrow(username);
        Note note = getNoteOrThrow(noteId);

        if (!note.getOwner().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to update this note");
        }

        note.setTitle(title);
        note.setContent(content);

        return noteRepository.save(note);
    }

    @Transactional
    public void deleteNote(Long noteId, String username) {
        User user = getUserOrThrow(username);
        Note note = getNoteOrThrow(noteId);

        if (!note.getOwner().equals(user)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this note");
        }

        noteRepository.delete(note);
    }

    @Transactional
    public Note shareNote(Long noteId, String targetUsername, String ownerUsername) {
        User owner = getUserOrThrow(ownerUsername);
        User targetUser = getUserOrThrow(targetUsername);
        Note note = getNoteOrThrow(noteId);

        if (!note.getOwner().equals(owner)) {
            throw new UnauthorizedAccessException("You don't have permission to share this note");
        }

        note.getSharedWith().add(targetUser);
        return noteRepository.save(note);
    }

    @Transactional(readOnly = true)
    public List<Note> searchNotes(String query, String username) {
        User user = getUserOrThrow(username);
        return noteRepository.searchNotes(query, user.getId());
    }

    private User getUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Note getNoteOrThrow(Long noteId) {
        return noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Note not found"));
    }

    private boolean canAccessNote(Note note, User user) {
        return note.getOwner().equals(user) || note.getSharedWith().contains(user);
    }
}
