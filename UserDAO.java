package org.example.seproject1.dao;

import org.example.seproject1.model.User;
import org.example.seproject1.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // =========================================================
    // 1. VALIDATE LOGIN
    // =========================================================
    // Checks Username OR Email through the Users compatibility view.
    public User validateUser(String username, String password) throws SQLException {

        String sql = """
                SELECT UserID, Username, Email, Role, Status
                FROM Users
                WHERE (Username = ? OR Email = ?)
                AND Password = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, password);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(rs.getInt("UserID"));
                    user.setUsername(rs.getString("Username"));
                    user.setEmail(rs.getString("Email"));
                    user.setRole(rs.getString("Role"));
                    user.setStatus(rs.getString("Status"));

                    return user;
                }
            }
        }

        return null;
    }


    // =========================================================
    // 2. AUTHENTICATE
    // =========================================================
    // Alias kept for backwards compatibility.
    public User authenticate(String username, String password)
            throws SQLException {

        return validateUser(username, password);
    }


    // =========================================================
    // 3. GET ALL USERS
    // =========================================================
    public List<User> getAllUsers() throws SQLException {

        List<User> list = new ArrayList<>();

        String sql = """
                SELECT UserID, Username, Email, Role, Status
                FROM Users
                ORDER BY UserID DESC
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                User user = new User();

                user.setUserId(rs.getInt("UserID"));
                user.setUsername(rs.getString("Username"));
                user.setEmail(rs.getString("Email"));
                user.setRole(rs.getString("Role"));
                user.setStatus(rs.getString("Status"));

                list.add(user);
            }
        }

        return list;
    }


    // =========================================================
    // 4. CREATE USER
    // =========================================================
    public boolean createUser(User user) throws SQLException {

        String sql = """
                INSERT INTO Users
                (Username, Password, Email, Role, Status)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(
                    1,
                    user.getUsername()
            );

            stmt.setString(
                    2,
                    user.getPassword() != null
                            ? user.getPassword()
                            : "DefaultPass123"
            );

            stmt.setString(
                    3,
                    user.getEmail()
            );

            stmt.setString(
                    4,
                    user.getRole() != null
                            ? user.getRole()
                            : "User"
            );

            stmt.setString(
                    5,
                    user.getStatus() != null
                            ? user.getStatus()
                            : "Active"
            );

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 5. UPDATE USER
    // =========================================================
    public boolean updateUser(User user) throws SQLException {

        String sql = """
                UPDATE Users
                SET Username = ?,
                    Email = ?,
                    Role = ?,
                    Status = ?
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(
                    1,
                    user.getUsername()
            );

            stmt.setString(
                    2,
                    user.getEmail()
            );

            stmt.setString(
                    3,
                    user.getRole()
            );

            stmt.setString(
                    4,
                    user.getStatus() != null
                            ? user.getStatus()
                            : "Active"
            );

            stmt.setInt(
                    5,
                    user.getUserId()
            );

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 6. DELETE USER
    // =========================================================
    public boolean deleteUser(int userId) throws SQLException {

        /*
         * If this user is linked to an employee, remove the
         * employee link first.
         *
         * This prevents the Employee -> User relationship
         * from blocking the deletion.
         */
        String unlinkSql = """
                UPDATE Employees
                SET UserID = NULL
                WHERE UserID = ?
                """;

        String deleteSql = """
                DELETE FROM Users
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // Remove Employee -> User relationship
                try (PreparedStatement unlinkStmt =
                             conn.prepareStatement(unlinkSql)) {

                    unlinkStmt.setInt(1, userId);
                    unlinkStmt.executeUpdate();
                }

                // Delete the account
                int affectedRows;

                try (PreparedStatement deleteStmt =
                             conn.prepareStatement(deleteSql)) {

                    deleteStmt.setInt(1, userId);
                    affectedRows = deleteStmt.executeUpdate();
                }

                conn.commit();

                return affectedRows > 0;

            } catch (SQLException e) {

                conn.rollback();
                throw e;

            } finally {

                conn.setAutoCommit(true);
            }
        }
    }


    // =========================================================
    // 7. UPDATE PASSWORD BY USERNAME OR EMAIL
    // =========================================================
    public boolean updatePassword(
            String usernameOrEmail,
            String newPassword) throws SQLException {

        String sql = """
                UPDATE Users
                SET Password = ?
                WHERE Username = ?
                   OR Email = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, usernameOrEmail);
            stmt.setString(3, usernameOrEmail);

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 8. UPDATE USER STATUS
    // =========================================================
    public boolean updateStatus(
            int userId,
            String status) throws SQLException {

        String sql = """
                UPDATE Users
                SET Status = ?
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 9. RESET PASSWORD BY USER ID
    // =========================================================
    public boolean updatePasswordById(
            int userId,
            String newPassword) throws SQLException {

        String sql = """
                UPDATE Users
                SET Password = ?
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 10. LINK USER ACCOUNT TO EMPLOYEE
    // =========================================================
    /*
     * IMPORTANT DATABASE RELATIONSHIP:
     *
     * Employees.UserID -> UserAccounts.UserID
     *
     * We therefore update Employees.UserID.
     *
     * The database already has:
     * UQ_Employees_UserID_NotNull
     *
     * which prevents one UserAccount from being linked
     * to multiple employees.
     */
    public boolean linkUserToEmployee(
            int userId,
            int employeeId) throws SQLException {

        // -----------------------------------------------------
        // Check that the user exists
        // -----------------------------------------------------
        String userCheckSql = """
                SELECT UserID
                FROM UserAccounts
                WHERE UserID = ?
                """;

        // -----------------------------------------------------
        // Check that the employee exists
        // -----------------------------------------------------
        String employeeCheckSql = """
                SELECT EmployeeID, UserID
                FROM Employees
                WHERE EmployeeID = ?
                """;

        // -----------------------------------------------------
        // Check whether the user is already linked
        // -----------------------------------------------------
        String existingEmployeeLinkSql = """
                SELECT EmployeeID
                FROM Employees
                WHERE UserID = ?
                """;

        // -----------------------------------------------------
        // Link the employee to the user
        // -----------------------------------------------------
        String updateSql = """
                UPDATE Employees
                SET UserID = ?
                WHERE EmployeeID = ?
                """;

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            try {

                // -------------------------------------------------
                // 1. Verify UserAccount exists
                // -------------------------------------------------
                try (PreparedStatement stmt =
                             conn.prepareStatement(userCheckSql)) {

                    stmt.setInt(1, userId);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }
                    }
                }


                // -------------------------------------------------
                // 2. Verify Employee exists
                // -------------------------------------------------
                Integer currentEmployeeUserId = null;

                try (PreparedStatement stmt =
                             conn.prepareStatement(employeeCheckSql)) {

                    stmt.setInt(1, employeeId);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (!rs.next()) {
                            conn.rollback();
                            return false;
                        }

                        int existingUserId =
                                rs.getInt("UserID");

                        if (!rs.wasNull()) {
                            currentEmployeeUserId =
                                    existingUserId;
                        }
                    }
                }


                // -------------------------------------------------
                // 3. If employee already belongs to this user,
                //    nothing needs to change.
                // -------------------------------------------------
                if (currentEmployeeUserId != null
                        && currentEmployeeUserId == userId) {

                    conn.commit();
                    return true;
                }


                // -------------------------------------------------
                // 4. Check whether this USER is already linked
                //    to another employee.
                // -------------------------------------------------
                try (PreparedStatement stmt =
                             conn.prepareStatement(
                                     existingEmployeeLinkSql)) {

                    stmt.setInt(1, userId);

                    try (ResultSet rs = stmt.executeQuery()) {

                        if (rs.next()) {

                            conn.rollback();

                            throw new SQLException(
                                    "This user account is already linked "
                                            + "to another employee."
                            );
                        }
                    }
                }


                // -------------------------------------------------
                // 5. Link Employee -> User
                // -------------------------------------------------
                try (PreparedStatement stmt =
                             conn.prepareStatement(updateSql)) {

                    stmt.setInt(1, userId);
                    stmt.setInt(2, employeeId);

                    int affectedRows =
                            stmt.executeUpdate();

                    if (affectedRows == 0) {
                        conn.rollback();
                        return false;
                    }
                }


                // -------------------------------------------------
                // 6. Commit
                // -------------------------------------------------
                conn.commit();

                return true;

            } catch (SQLException e) {

                conn.rollback();
                throw e;

            } finally {

                conn.setAutoCommit(true);
            }
        }
    }


    // =========================================================
    // 11. UNLINK USER ACCOUNT FROM EMPLOYEE
    // =========================================================
    /*
     * Removes ONLY the Employee -> User relationship.
     *
     * It does NOT:
     * - delete the employee
     * - delete the user account
     * - delete attendance
     * - delete leave records
     * - delete payroll
     * - delete performance records
     *
     * It simply changes:
     *
     * Employees.UserID = NULL
     *
     */
    public boolean unlinkUserFromEmployee(
            int userId) throws SQLException {

        String sql = """
                UPDATE Employees
                SET UserID = NULL
                WHERE UserID = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            return stmt.executeUpdate() > 0;
        }
    }


    // =========================================================
    // 12. LOG ACTION
    // =========================================================
    public void logAction(
            int userId,
            String description) {

        String sql = """
                INSERT INTO AuditLogs
                (UserID, ActionDescription, LogTimestamp)
                VALUES (?, ?, GETDATE())
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, description);

            stmt.executeUpdate();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}