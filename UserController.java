package org.example.seproject1.controller;

import jakarta.servlet.http.HttpSession;
import org.example.seproject1.dao.UserDAO;
import org.example.seproject1.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserDAO userDAO = new UserDAO();

    // Helper method to retrieve the real logged-in user ID from session
    private int getCurrentUserId(HttpSession session) {
        if (session != null && session.getAttribute("userId") != null) {
            return (Integer) session.getAttribute("userId");
        }

        return 1; // Fallback ID if session isn't found
    }

    // =========================
    // GET ALL USERS
    // =========================
    @GetMapping
    public ResponseEntity<?> getAllUsers() {

        try {
            List<User> users = userDAO.getAllUsers();

            return ResponseEntity.ok(users);

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving users: " + e.getMessage());
        }
    }

    // =========================
    // CREATE USER
    // =========================
    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody User user,
            HttpSession session) {

        try {

            boolean success = userDAO.createUser(user);

            if (success) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin created a new user account (" + user.getUsername() + ")"
                );

                return ResponseEntity.ok(
                        "User created successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to create user");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving user: " + e.getMessage());
        }
    }

    // =========================
    // UPDATE USER
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable int id,
            @RequestBody User user,
            HttpSession session) {

        try {

            user.setUserId(id);

            boolean updated = userDAO.updateUser(user);

            if (updated) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin updated details for user ID " + id
                );

                return ResponseEntity.ok(
                        "User updated successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }

    // =========================
    // DELETE USER
    // =========================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable int id,
            HttpSession session) {

        try {

            boolean deleted = userDAO.deleteUser(id);

            if (deleted) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin deleted user ID " + id
                );

                return ResponseEntity.ok(
                        "User deleted successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting user: " + e.getMessage());
        }
    }

    // =========================
    // CHANGE PASSWORD
    // =========================
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        try {

            String usernameOrEmail = payload.get("usernameOrEmail");
            String newPassword = payload.get("newPassword");

            if (usernameOrEmail == null
                    || newPassword == null
                    || newPassword.trim().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid username/email or password.");
            }

            boolean updated =
                    userDAO.updatePassword(usernameOrEmail, newPassword);

            if (updated) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin updated password for account: "
                                + usernameOrEmail
                );

                return ResponseEntity.ok(
                        "Password updated successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating password: " + e.getMessage());
        }
    }

    // =========================
    // UPDATE USER STATUS
    // =========================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable int id,
            @RequestBody Map<String, String> payload,
            HttpSession session) {

        try {

            String status = payload.get("status");

            if (status == null || status.trim().isEmpty()) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Status is required");
            }

            boolean updated =
                    userDAO.updateStatus(id, status);

            if (updated) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin updated user ID " + id
                                + " status to " + status
                );

                return ResponseEntity.ok(
                        "User status updated successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user status: " + e.getMessage());
        }
    }

    // =========================
    // RESET PASSWORD
    // =========================
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(
            @PathVariable int id,
            HttpSession session) {

        try {

            String tempPassword = "TempPassword123!";

            boolean updated =
                    userDAO.updatePasswordById(id, tempPassword);

            if (updated) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin reset password for user ID " + id
                );

                return ResponseEntity.ok(
                        Map.of(
                                "message",
                                "Password reset successfully. Temporary password: "
                                        + tempPassword
                        )
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting password: " + e.getMessage());
        }
    }

    // =========================
    // LINK USER TO EMPLOYEE
    // =========================
    @PutMapping("/{userId}/link-employee/{employeeId}")
    public ResponseEntity<?> linkEmployee(
            @PathVariable int userId,
            @PathVariable int employeeId,
            HttpSession session) {

        try {

            boolean linked =
                    userDAO.linkUserToEmployee(userId, employeeId);

            if (linked) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin linked user ID " + userId
                                + " to employee ID " + employeeId
                );

                return ResponseEntity.ok(
                        "Employee linked successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to link employee to user");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error linking employee: " + e.getMessage());
        }
    }

    // =========================
    // UNLINK USER FROM EMPLOYEE
    // =========================
    @PutMapping("/{userId}/unlink-employee")
    public ResponseEntity<?> unlinkEmployee(
            @PathVariable int userId,
            HttpSession session) {

        try {

            boolean unlinked =
                    userDAO.unlinkUserFromEmployee(userId);

            if (unlinked) {

                int currentId = getCurrentUserId(session);

                userDAO.logAction(
                        currentId,
                        "Admin unlinked user ID " + userId
                                + " from employee"
                );

                return ResponseEntity.ok(
                        "Employee unlinked successfully"
                );

            } else {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No employee was linked to this user");
            }

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error unlinking employee: " + e.getMessage());
        }
    }
}