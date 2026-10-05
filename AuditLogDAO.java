package org.example.seproject1.dao;

import org.example.seproject1.model.AuditLog;
import org.example.seproject1.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    public List<AuditLog> getAllLogs() throws SQLException {
        List<AuditLog> list = new ArrayList<>();
        String sql = "SELECT LogID, UserID, ActionDescription, LogTimestamp FROM AuditLogs ORDER BY LogID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("LogID"));
                log.setUserId(rs.getInt("UserID"));
                log.setActionDescription(rs.getString("ActionDescription"));
                log.setLogTimestamp(rs.getTimestamp("LogTimestamp"));
                list.add(log);
            }
        }
        return list;
    }
}