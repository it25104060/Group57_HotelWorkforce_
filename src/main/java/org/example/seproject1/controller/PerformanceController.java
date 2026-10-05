package org.example.seproject1.controller;

import org.example.seproject1.dao.PerformanceDAO;
import org.example.seproject1.model.Performance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "*")
public class PerformanceController {

    private final PerformanceDAO performanceDAO = new PerformanceDAO();

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            List<Performance> list = performanceDAO.getAllReviews();
            return ResponseEntity.ok(list);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching performance reviews: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody Performance performance) {
        try {
            boolean success = performanceDAO.addReview(performance);
            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Performance review created successfully.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create review.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReview(@PathVariable int id, @RequestBody Performance performance) {
        try {
            performance.setReviewId(id);
            boolean success = performanceDAO.updateReview(performance);
            if (success) {
                return ResponseEntity.ok("Performance review updated successfully.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReview(@PathVariable int id) {
        try {
            boolean success = performanceDAO.deleteReview(id);
            if (success) {
                return ResponseEntity.ok("Performance review deleted successfully.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }
}