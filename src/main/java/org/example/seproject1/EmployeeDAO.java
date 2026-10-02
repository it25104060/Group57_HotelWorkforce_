package org.example.seproject1.dao;

import org.example.seproject1.model.Employee;
import org.example.seproject1.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // 1. READ ALL EMPLOYEES
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String query = "SELECT EmployeeID, EmployeeCode, UserID, FullName, NIC, DOB, Phone, Email, " +
                "Address, DepartmentID, PositionID, HireDate, BaseSalary, AnnualLeaveEntitlement, Status " +
                "FROM Employees ORDER BY EmployeeID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToEmployee(rs));
            }
        }
        return list;
    }

    // 2. CHECK IF NIC EXISTS (For Duplicate Validation)
    public boolean isNicExists(String nic, Integer excludeEmployeeId) throws SQLException {
        if (nic == null || nic.trim().isEmpty()) return false;
        String query = "SELECT COUNT(*) FROM Employees WHERE NIC = ? AND (? IS NULL OR EmployeeID <> ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nic.trim());
            if (excludeEmployeeId != null) {
                stmt.setInt(2, excludeEmployeeId);
                stmt.setInt(3, excludeEmployeeId);
            } else {
                stmt.setNull(2, Types.INTEGER);
                stmt.setNull(3, Types.INTEGER);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    // 3. CREATE / INSERT EMPLOYEE
    public boolean addEmployee(Employee emp) throws SQLException {
        if (emp.getNic() == null || emp.getNic().trim().isEmpty() || emp.getNic().equals("<blank>")) {
            throw new SQLException("Validation Error: NIC is a mandatory field and cannot be blank.");
        }
        if (isNicExists(emp.getNic(), null)) {
            throw new SQLException("Validation Error: An employee with NIC '" + emp.getNic() + "' already exists.");
        }

        String query = "INSERT INTO Employees (EmployeeCode, UserID, FullName, NIC, DOB, Phone, Email, Address, " +
                "DepartmentID, PositionID, HireDate, BaseSalary, AnnualLeaveEntitlement, Status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emp.getEmployeeCode());
            if (emp.getUserId() != null) stmt.setInt(2, emp.getUserId()); else stmt.setNull(2, Types.INTEGER);
            stmt.setString(3, emp.getFullName());
            stmt.setString(4, emp.getNic().trim());
            stmt.setString(5, emp.getDob() != null && !emp.getDob().isEmpty() ? emp.getDob() : LocalDate.now().toString());
            stmt.setString(6, emp.getPhone() != null ? emp.getPhone() : "N/A");
            stmt.setString(7, emp.getEmail());
            stmt.setString(8, emp.getAddress() != null && !emp.getAddress().isEmpty() ? emp.getAddress() : "N/A");
            stmt.setInt(9, emp.getDepartmentId());
            stmt.setInt(10, emp.getPositionId());
            stmt.setString(11, emp.getHireDate() != null && !emp.getHireDate().isEmpty() ? emp.getHireDate() : LocalDate.now().toString());
            stmt.setDouble(12, emp.getBaseSalary());
            stmt.setInt(13, emp.getAnnualLeaveEntitlement());
            stmt.setString(14, emp.getStatus() != null ? emp.getStatus() : "Active");

            return stmt.executeUpdate() > 0;
        }
    }

    // 4. UPDATE EMPLOYEE
    public boolean updateEmployee(Employee emp) throws SQLException {
        if (emp.getNic() != null && isNicExists(emp.getNic(), emp.getEmployeeId())) {
            throw new SQLException("Validation Error: Duplicate NIC detected for another employee record.");
        }

        String query = "UPDATE Employees SET FullName = ?, NIC = ?, DOB = ?, Phone = ?, Email = ?, Address = ?, " +
                "DepartmentID = ?, PositionID = ?, BaseSalary = ?, AnnualLeaveEntitlement = ?, Status = ? " +
                "WHERE EmployeeID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, emp.getFullName());
            stmt.setString(2, emp.getNic());
            stmt.setString(3, emp.getDob());
            stmt.setString(4, emp.getPhone() != null ? emp.getPhone() : "N/A");
            stmt.setString(5, emp.getEmail());
            stmt.setString(6, emp.getAddress() != null ? emp.getAddress() : "N/A");
            stmt.setInt(7, emp.getDepartmentId());
            stmt.setInt(8, emp.getPositionId());
            stmt.setDouble(9, emp.getBaseSalary());
            stmt.setInt(10, emp.getAnnualLeaveEntitlement());
            stmt.setString(11, emp.getStatus());
            stmt.setInt(12, emp.getEmployeeId());

            return stmt.executeUpdate() > 0;
        }
    }

    // 5. DELETE EMPLOYEE (Clears child dependencies safely)
    public boolean deleteEmployee(int employeeId) throws SQLException {
        String deletePayrollSql = "DELETE FROM Payroll WHERE EmployeeID = ?";
        String deleteAttendanceSql = "DELETE FROM Attendance WHERE EmployeeID = ?";
        String deleteEmployeeSql = "DELETE FROM Employees WHERE EmployeeID = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement stmt1 = conn.prepareStatement(deletePayrollSql)) {
                    stmt1.setInt(1, employeeId);
                    stmt1.executeUpdate();
                }
                try (PreparedStatement stmt2 = conn.prepareStatement(deleteAttendanceSql)) {
                    stmt2.setInt(1, employeeId);
                    stmt2.executeUpdate();
                }
                int rowsAffected;
                try (PreparedStatement stmt3 = conn.prepareStatement(deleteEmployeeSql)) {
                    stmt3.setInt(1, employeeId);
                    rowsAffected = stmt3.executeUpdate();
                }
                conn.commit();
                return rowsAffected > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // 6. AUDIT LOGGING HELPER
    public void logAction(int userId, String description) {
        String sql = "INSERT INTO AuditLogs (UserID, ActionDescription, LogTimestamp) VALUES (?, ?, GETDATE())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId > 0 ? userId : 1);
            stmt.setString(2, description);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper mapper method
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeId(rs.getInt("EmployeeID"));
        emp.setEmployeeCode(rs.getString("EmployeeCode"));
        emp.setUserId(rs.getObject("UserID") != null ? rs.getInt("UserID") : null);
        emp.setFullName(rs.getString("FullName"));
        emp.setNic(rs.getString("NIC"));
        emp.setDob(rs.getString("DOB"));
        emp.setPhone(rs.getString("Phone"));
        emp.setEmail(rs.getString("Email"));
        emp.setAddress(rs.getString("Address"));
        emp.setDepartmentId(rs.getInt("DepartmentID"));
        emp.setPositionId(rs.getInt("PositionID"));
        emp.setHireDate(rs.getString("HireDate"));
        emp.setBaseSalary(rs.getDouble("BaseSalary"));
        emp.setAnnualLeaveEntitlement(rs.getInt("AnnualLeaveEntitlement"));
        emp.setStatus(rs.getString("Status"));
        return emp;
    }
}