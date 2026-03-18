package com.sms.security;

import com.sms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * SecurityConfig — configures Spring Security with role-based access control.
 * ADMIN: all routes. TEACHER: view students, attendance, marks, notices, timetable.
 * STUDENT: own dashboard or own data only.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()

                // ADMIN: all routes (handled by catch-all below + specific)
                // STUDENT-only routes
                .requestMatchers("/student-dashboard").hasRole("STUDENT")

                // Dashboard for ADMIN and TEACHER
                .requestMatchers("/dashboard").hasAnyRole("ADMIN", "TEACHER")

                // Student CRUD (add/edit/delete) — ADMIN only
                .requestMatchers("/students/add", "/students/edit/**", "/students/delete/**").hasRole("ADMIN")

                // Student list and profile — ADMIN and TEACHER
                .requestMatchers("/students", "/students/profile/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                // Attendance — ADMIN and TEACHER can mark, all can view
                .requestMatchers("/attendance/mark").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/attendance/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                // Marks — ADMIN and TEACHER can add
                .requestMatchers("/marks/add").hasAnyRole("ADMIN", "TEACHER")
                .requestMatchers("/marks/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                // Fees — ADMIN can add
                .requestMatchers("/fees/add").hasRole("ADMIN")
                .requestMatchers("/fees/**").hasAnyRole("ADMIN", "STUDENT")

                // Notices — ADMIN can add/delete
                .requestMatchers("/notices/add", "/notices/delete/**").hasRole("ADMIN")
                .requestMatchers("/notices/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                // Timetable — ADMIN can add/delete
                .requestMatchers("/timetable/add", "/timetable/delete/**").hasRole("ADMIN")
                .requestMatchers("/timetable/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                // Reports — ADMIN only
                .requestMatchers("/reports/**").hasRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated())

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login?error=true")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if (role.equals("ROLE_STUDENT")) {
                        response.sendRedirect("/student-dashboard");
                    } else {
                        response.sendRedirect("/dashboard");
                    }
                })
                .permitAll())

            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll());

        return http.build();
    }
}
