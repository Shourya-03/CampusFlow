package com.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AuthController — handles authentication-related routes.
 *
 * Routes:
 *  GET /login  — shows the login page (with optional error/logout flash messages)
 *
 * Note: The actual login POST (/login) is handled automatically by Spring Security.
 * We only need a controller to SHOW the login form via GET.
 */
@Controller
public class AuthController {

    /**
     * Displays the login page.
     *
     * @param error  present in URL if login failed (/login?error=true)
     * @param logout present in URL if user just logged out (/login?logout=true)
     * @param model  Thymeleaf model to pass data to the template
     * @return the login template (templates/login.html)
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        // Pass error flag — Thymeleaf template shows "Invalid credentials" message
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }

        // Pass logout flag — Thymeleaf template shows "Logged out successfully" message
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }

        return "login"; // Resolves to src/main/resources/templates/login.html
    }
}
