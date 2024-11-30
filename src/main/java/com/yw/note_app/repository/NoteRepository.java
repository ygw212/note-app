package com.yw.note_app.repository;

import com.yw.note_app.model.Note;
import com.yw.note_app.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Page<Note> findByOwnerOrSharedWith(User owner, User sharedUser, Pageable pageable);

    @Query("SELECT n FROM Note n WHERE (n.owner.id = :userId OR :userId IN (SELECT u.id FROM n.sharedWith u)) " +
            "AND (LOWER(n.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(n.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Note> searchNotes(@Param("query") String query, @Param("userId") Long userId);
}