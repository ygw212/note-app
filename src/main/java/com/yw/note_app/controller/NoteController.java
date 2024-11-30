package com.yw.note_app.controller;

import com.yw.note_app.dto.mapper.NoteMapper;
import com.yw.note_app.dto.request.NoteRequest;
import com.yw.note_app.dto.request.ShareNoteRequest;
import com.yw.note_app.dto.response.ApiResponseDto;
import com.yw.note_app.dto.response.NoteResponse;
import com.yw.note_app.dto.response.PagedResponse;
import com.yw.note_app.model.Note;
import com.yw.note_app.service.NoteService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@Tag(name = "Notes", description = "Note management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NoteController {
    private final NoteService noteService;
    private final NoteMapper noteMapper;

    public NoteController(NoteService noteService, NoteMapper noteMapper) {
        this.noteService = noteService;
        this.noteMapper = noteMapper;
    }
    @Operation(
            summary = "Get all notes",
            description = "Retrieve all notes accessible to the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved notes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    //get all notes
    @GetMapping("/notes")
    public ResponseEntity<PagedResponse<NoteResponse>> getAllNotes(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Note> notePage = noteService.getAllNotes(authentication.getName(), pageable);

        List<NoteResponse> notes = notePage.getContent().stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());

        PagedResponse<NoteResponse> response = new PagedResponse<>(
                notes,
                notePage.getNumber(),
                notePage.getSize(),
                notePage.getTotalElements(),
                notePage.getTotalPages(),
                notePage.isLast()
        );

        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "Get note",
            description = "Get a note by its id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully got the note"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    //get a note by its id
    @GetMapping("/notes/{id}")
    public ResponseEntity<NoteResponse> getNoteById(
            @PathVariable Long id,
            Authentication authentication) {
        Note note = noteService.getNoteById(id, authentication.getName());
        return ResponseEntity.ok(noteMapper.toNoteResponse(note));
    }
    @Operation(
            summary = "Create note",
            description = "Create a note"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created the note"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    //create a note
    @PostMapping("/notes")
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestBody NoteRequest noteRequest,
            Authentication authentication) {
        Note note = noteService.createNote(
                noteRequest.getTitle(),
                noteRequest.getContent(),
                authentication.getName()
        );
        return ResponseEntity.ok(noteMapper.toNoteResponse(note));
    }

    @Operation(
            summary = "Update note",
            description = "Update a note by its id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully updated the note"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    //update a note
    @PutMapping("/notes/{id}")
    public ResponseEntity<NoteResponse> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequest noteRequest,
            Authentication authentication) {
        Note note = noteService.updateNote(
                id,
                noteRequest.getTitle(),
                noteRequest.getContent(),
                authentication.getName()
        );
        return ResponseEntity.ok(noteMapper.toNoteResponse(note));
    }

    @Operation(
            summary = "Delete note",
            description = "Delete a note by its id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully deleted the note"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    //delete a note
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<ApiResponseDto> deleteNote(
            @PathVariable Long id,
            Authentication authentication) {
        noteService.deleteNote(id, authentication.getName());
        return ResponseEntity.ok(new ApiResponseDto(true, "Note deleted successfully"));
    }

    @Operation(
            summary = "Share note",
            description = "Share a note with another user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Note shared successfully"),
            @ApiResponse(responseCode = "404", description = "Note not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    //share a note
    @PostMapping("/notes/{id}/share")
    public ResponseEntity<NoteResponse> shareNote(
            @PathVariable Long id,
            @Valid @RequestBody ShareNoteRequest shareRequest,
            Authentication authentication) {
        Note note = noteService.shareNote(
                id,
                shareRequest.getUsername(),
                authentication.getName()
        );
        return ResponseEntity.ok(noteMapper.toNoteResponse(note));
    }

    @Operation(
            summary = "Search notes",
            description = "Search notes using full-text search"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search completed successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "429", description = "Too many requests")
    })
    //search notes
    @GetMapping("/search")
    public ResponseEntity<List<NoteResponse>> searchNotes(
            @RequestParam String q,
            Authentication authentication) {
        List<Note> notes = noteService.searchNotes(q, authentication.getName());
        List<NoteResponse> responses = notes.stream()
                .map(noteMapper::toNoteResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
