package org.example.seproject1.controller;

import org.example.seproject1.dao.LeaveDAO;
import org.example.seproject1.model.LeaveRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = "*")
public class LeaveController {

    private final LeaveDAO leaveDAO = new LeaveDAO();

    // 1. Submit a new leave request
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@RequestBody LeaveRequest leaveRequest) {
        try {
            boolean success = leaveDAO.submitLeaveRequest(leaveRequest);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Leave request submitted successfully."));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to submit leave request."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 2. Fetch leave requests for a single employee
    @GetMapping("/employee/{id}")
    public ResponseEntity<?> getEmployeeLeaves(@PathVariable("id") int employeeId) {
        try {
            List<LeaveRequest> leaves = leaveDAO.getLeaveRequestsByEmployee(employeeId);
            return ResponseEntity.ok(leaves);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 3. Fetch all leave requests (for Manager / HR dashboard)
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves() {
        try {
            List<LeaveRequest> leaves = leaveDAO.getAllLeaveRequests();
            return ResponseEntity.ok(leaves);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 4. Update leave request status (Approve / Reject)
    @PutMapping("/status")
    public ResponseEntity<?> updateStatus(@RequestBody Map<String, Object> payload) {
        try {
            int leaveId = Integer.parseInt(payload.get("leaveId").toString());
            String status = payload.get("status").toString();

            boolean success = leaveDAO.updateLeaveStatus(leaveId, status);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Leave status updated to " + status));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to update leave status."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    // 5. Delete a leave request permanently
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeave(@PathVariable("id") int leaveId) {
        try {
            boolean success = leaveDAO.deleteLeaveRequest(leaveId);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "Leave request deleted successfully."));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to delete leave request or record not found."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}