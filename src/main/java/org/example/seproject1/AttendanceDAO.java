package org.example.seproject1.dao;

import org.example.seproject1.model.Attendance;
import org.example.seproject1.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    // 1. READ ALL ATTENDANCE LOGS
    public List<Attendance> getAllAttendance() throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.AttendanceID, a.EmployeeID, e.FullName, a.Date, " +
                "a.ClockInTime, a.ClockOutTime, a.WorkHours, a.Status " +
                "FROM Attendance a " +
                "JOIN Employees e ON a.EmployeeID = e.EmployeeID " +
                "ORDER BY a.Date DESC, a.AttendanceID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Attendance att = new Attendance();
                att.setAttendanceId(rs.getInt("AttendanceID"));
                att.setEmployeeId(rs.getInt("EmployeeID"));
                att.setEmployeeName(rs.getString("FullName"));
                att.setDate(rs.getString("Date"));
                att.setClockInTime(rs.getString("ClockInTime"));
                att.setClockOutTime(rs.getString("ClockOutTime"));
                att.setWorkHours(rs.getDouble("WorkHours"));
                att.setStatus(rs.getString("Status"));
                list.add(att);
            }
        }
        return list;
    }

    // 2. CREATE ATTENDANCE RECORD
    public boolean createAttendance(Attendance att) throws SQLException {
        String sql = "INSERT INTO Attendance (EmployeeID, Date, ClockInTime, ClockOutTime, WorkHours, Status) " +
                "VALUES (?, CAST(GETDATE() AS DATE), ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, att.getEmployeeId());
            stmt.setString(2, (att.getClockInTime() != null && !att.getClockInTime().trim().isEmpty()) ? att.getClockInTime() : null);
            stmt.setString(3, (att.getClockOutTime() != null && !att.getClockOutTime().trim().isEmpty()) ? att.getClockOutTime() : null);
            stmt.setDouble(4, att.getWorkHours() != null ? att.getWorkHours() : 0.0);
            stmt.setString(5, (att.getStatus() != null && !att.getStatus().trim().isEmpty()) ? att.getStatus() : "Present");

            return stmt.executeUpdate() > 0;
        }
    }

    // 3. RECORD CLOCK-IN
    public boolean recordClockIn(int employeeId, String status) throws SQLException {
        String sql = "INSERT INTO Attendance (EmployeeID, Date, ClockInTime, Status) " +
                "VALUES (?, CAST(GETDATE() AS DATE), CAST(GETDATE() AS TIME), ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeId);
            stmt.setString(2, status != null ? status : "Present");

            return stmt.executeUpdate() > 0;
        }
    }

    // 4. RECORD CLOCK-OUT
    public boolean recordClockOut(int attendanceId, double workHours) throws SQLException {
        String sql = "UPDATE Attendance SET ClockOutTime = CAST(GETDATE() AS TIME), WorkHours = ? " +
                "WHERE AttendanceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, workHours);
            stmt.setInt(2, attendanceId);

            return stmt.executeUpdate() > 0;
        }
    }

    // 5. UPDATE ATTENDANCE RECORD (PUT)
    public boolean updateAttendance(Attendance att) throws SQLException {
        String sql = "UPDATE Attendance SET ClockInTime = ?, ClockOutTime = ?, WorkHours = ?, Status = ? " +
                "WHERE AttendanceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, (att.getClockInTime() != null && !att.getClockInTime().trim().isEmpty()) ? att.getClockInTime() : null);
            stmt.setString(2, (att.getClockOutTime() != null && !att.getClockOutTime().trim().isEmpty()) ? att.getClockOutTime() : null);
            stmt.setDouble(3, att.getWorkHours() != null ? att.getWorkHours() : 0.0);
            stmt.setString(4, (att.getStatus() != null && !att.getStatus().trim().isEmpty()) ? att.getStatus() : "Present");
            stmt.setInt(5, att.getAttendanceId());

            return stmt.executeUpdate() > 0;
        }
    }

    // 6. DELETE ATTENDANCE RECORD (DELETE)
    public boolean deleteAttendance(int attendanceId) throws SQLException {
        String sql = "DELETE FROM Attendance WHERE AttendanceID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attendanceId);
            return stmt.executeUpdate() > 0;
        }
    }
}
