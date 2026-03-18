package com.sms.service;

import com.sms.model.User;
import com.sms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * UserService — implements Spring Security's UserDetailsService interface.
 * This is the bridge between Spring Security and our User database.
 * When a user tries to log in, Spring Security calls loadUserByUsername()
 * to fetch user credentials and roles for authentication.
 */
@Service
public class UserService implements UserDetailsService {

    // Spring injects the UserRepository automatically
    @Autowired
    private UserRepository userRepository;

    /**
     * Loads a user by their username for Spring Security authentication.
     * The returned UserDetails object contains the username, hashed password,
     * and granted authorities (roles prefixed with "ROLE_").
     *
     * @param username the username entered at the login form
     * @return UserDetails with credentials and role
     * @throws UsernameNotFoundException if no user found with that username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Look up user in the database; throw exception if not found
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("No user found with username: " + username));

        // Spring Security expects roles prefixed with "ROLE_"
        // e.g., ADMIN → ROLE_ADMIN, TEACHER → ROLE_TEACHER, STUDENT → ROLE_STUDENT
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name());

        // Return a Spring Security User object with username, hashed password, and role
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}
