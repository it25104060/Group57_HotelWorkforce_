package org.example.seproject1.dao;

import org.example.seproject1.model.Performance;
import org.example.seproject1.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PerformanceDAO {

    // 1. READ ALL REVIEWS (Joined with Employees to get Full Name)
    public List<Performance> getAllReviews() throws SQLException {
        List<Performance> list = new ArrayList<>();
        String sql = "SELECT p.ReviewID, p.EmployeeID, e.FullName, p.ReviewDate, " +
                "p.ReviewPeriod, p.Rating, p.Feedback, p.ReviewerName, p.Status " +
                "FROM PerformanceReviews p " +
                "JOIN Employees e ON p.EmployeeID = e.EmployeeID " +
                "ORDER BY p.ReviewID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Performance p = new Performance();
                p.setReviewId(rs.getInt("ReviewID"));
                p.setEmployeeId(rs.getInt("EmployeeID"));
                p.setEmployeeName(rs.getString("FullName"));
                p.setReviewDate(rs.getString("ReviewDate"));
                p.setReviewPeriod(rs.getString("ReviewPeriod"));
                p.setRating(rs.getInt("Rating"));
                p.setFeedback(rs.getString("Feedback"));
                p.setReviewerName(rs.getString("ReviewerName"));
                p.setStatus(rs.getString("Status"));
                list.add(p);
            }
        }
        return list;
    }

    // 2. CREATE REVIEW
    public boolean addReview(Performance p) throws SQLException {
        String sql = "INSERT INTO PerformanceReviews (EmployeeID, ReviewDate, ReviewPeriod, Rating, Feedback, ReviewerName, Status) " +
                "VALUES (?, CAST(GETDATE() AS DATE), ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, p.getEmployeeId());
            stmt.setString(2, p.getReviewPeriod() != null ? p.getReviewPeriod() : "Annual Review");
            stmt.setInt(3, p.getRating());
            stmt.setString(4, p.getFeedback() != null ? p.getFeedback() : "");
            stmt.setString(5, p.getReviewerName() != null ? p.getReviewerName() : "Manager");
            stmt.setString(6, p.getStatus() != null ? p.getStatus() : "Completed");

            return stmt.executeUpdate() > 0;
        }
    }

    // 3. UPDATE REVIEW
    public boolean updateReview(Performance p) throws SQLException {
        String sql = "UPDATE PerformanceReviews SET ReviewPeriod = ?, Rating = ?, Feedback = ?, ReviewerName = ?, Status = ? " +
                "WHERE ReviewID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.getReviewPeriod());
            stmt.setInt(2, p.getRating());
            stmt.setString(3, p.getFeedback());
            stmt.setString(4, p.getReviewerName());
            stmt.setString(5, p.getStatus());
            stmt.setInt(6, p.getReviewId());

            return stmt.executeUpdate() > 0;
        }
    }

    // 4. DELETE REVIEW
    public boolean deleteReview(int reviewId) throws SQLException {
        String sql = "DELETE FROM PerformanceReviews WHERE ReviewID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reviewId);
            return stmt.executeUpdate() > 0;
        }
    }
}