package com.sms.repository;

import com.sms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * UserRepository — data access layer for the User entity.
 * Spring Data JPA auto-implements CRUD methods at runtime.
 * findByUsername is used by Spring Security to load user details during login.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Finds a user by their username (case-sensitive).
     * Returns Optional.empty() if no user is found with that username.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found
     */
    Optional<User> findByUsername(String username);
}
