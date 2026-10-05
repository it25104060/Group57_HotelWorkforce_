package org.example.seproject1.dao;

import org.example.seproject1.model.LeaveRequest;
import org.example.seproject1.util.DBConnection; // Verify if your connection class is named DBConnection or DatabaseConnection

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    // 1. Submit a new leave request
    public boolean submitLeaveRequest(LeaveRequest leave) throws SQLException {
        String sql = "INSERT INTO LeaveRequests (EmployeeID, LeaveType, StartDate, EndDate, DurationDays, Reason, Status) VALUES (?, ?, ?, ?, ?, ?, 'Pending')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, leave.getEmployeeID());
            stmt.setString(2, leave.getLeaveType());
            stmt.setDate(3, Date.valueOf(leave.getStartDate()));
            stmt.setDate(4, Date.valueOf(leave.getEndDate()));
            stmt.setInt(5, leave.getDurationDays());
            stmt.setString(6, leave.getReason());

            return stmt.executeUpdate() > 0;
        }
    }

    // 2. Fetch leave requests for a specific employee
    public List<LeaveRequest> getLeaveRequestsByEmployee(int employeeID) throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM LeaveRequests WHERE EmployeeID = ? ORDER BY StartDate DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                LeaveRequest leave = extractLeaveFromResultSet(rs);
                list.add(leave);
            }
        }
        return list;
    }

    // 3. Fetch all leave requests (for HR / Admin Manager dashboard)
    public List<LeaveRequest> getAllLeaveRequests() throws SQLException {
        List<LeaveRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM LeaveRequests ORDER BY StartDate DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LeaveRequest leave = extractLeaveFromResultSet(rs);
                list.add(leave);
            }
        }
        return list;
    }

    // 4. Update leave request status (Approve / Reject)
    public boolean updateLeaveStatus(int leaveID, String status) throws SQLException {
        String sql = "UPDATE LeaveRequests SET Status = ? WHERE LeaveID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, leaveID);

            return stmt.executeUpdate() > 0;
        }
    }

    // 5. Delete a leave request permanently
    public boolean deleteLeaveRequest(int leaveID) throws SQLException {
        String sql = "DELETE FROM LeaveRequests WHERE LeaveID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, leaveID);

            return stmt.executeUpdate() > 0;
        }
    }

    // Helper method to map ResultSet row to LeaveRequest Object
    private LeaveRequest extractLeaveFromResultSet(ResultSet rs) throws SQLException {
        LeaveRequest leave = new LeaveRequest();
        leave.setLeaveID(rs.getInt("LeaveID"));
        leave.setEmployeeID(rs.getInt("EmployeeID"));
        leave.setLeaveType(rs.getString("LeaveType"));
        leave.setStartDate(rs.getDate("StartDate").toLocalDate());
        leave.setEndDate(rs.getDate("EndDate").toLocalDate());
        leave.setDurationDays(rs.getInt("DurationDays"));
        leave.setReason(rs.getString("Reason"));
        leave.setStatus(rs.getString("Status"));
        return leave;
    }
}