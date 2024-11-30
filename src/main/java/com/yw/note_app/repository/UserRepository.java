package com.yw.note_app.repository;

import com.yw.note_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // find a user by the username
    Optional<User> findByUsername(String username);

    // check if the username exists
    boolean existsByUsername(String username);

}
