package org.example.seproject1.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.example.seproject1.dao.UserDAO;
import org.example.seproject1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserDAO userDAO = new UserDAO();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData, HttpServletRequest request) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        try {
            User user = userDAO.validateUser(username, password);

            if (user != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("loggedInUser", user.getUsername());
                session.setAttribute("userRole", user.getRole());
                session.setAttribute("userId", user.getUserId()); // <--- CRITICAL: Stores actual user ID in session

                // Automatically log the login event in AuditLogs
                userDAO.logAction(user.getUserId(), "User '" + user.getUsername() + "' successfully logged into the system.");

                return ResponseEntity.ok(Map.of(
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication error: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, jakarta.servlet.http.HttpSession session) {
        String loggedInUser = (session != null) ? (String) session.getAttribute("loggedInUser") : null;
        String username = request.get("username");
        String newPassword = request.get("newPassword");

        if (username == null || username.trim().isEmpty()) {
            username = loggedInUser;
        }

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User session not found. Please log in again.");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("New password is required.");
        }

        try {
            boolean updated = userDAO.updatePassword(username, newPassword);
            if (updated) {
                return ResponseEntity.ok("Password updated successfully!");
            } else {
                return ResponseEntity.status(404).body("User record not found for: " + username);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database error: " + e.getMessage());
        }
    }
}
