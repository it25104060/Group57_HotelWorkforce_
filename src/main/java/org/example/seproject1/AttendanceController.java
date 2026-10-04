package org.example.seproject1.controller;

import org.example.seproject1.dao.AttendanceDAO;
import org.example.seproject1.model.Attendance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    @GetMapping
    public ResponseEntity<?> getAllAttendance() {
        try {
            List<Attendance> list = attendanceDAO.getAllAttendance();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching attendance: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createAttendance(@RequestBody Map<String, Object> payload) {
        try {
            Attendance att = new Attendance();
            att.setEmployeeId(Integer.parseInt(payload.get("employeeId").toString()));
            att.setClockInTime(payload.get("clockInTime") != null ? payload.get("clockInTime").toString() : null);
            att.setClockOutTime(payload.get("clockOutTime") != null ? payload.get("clockOutTime").toString() : null);
            att.setStatus(payload.get("status") != null ? payload.get("status").toString() : "Present");

            if (payload.containsKey("workHours") && payload.get("workHours") != null) {
                att.setWorkHours(Double.parseDouble(payload.get("workHours").toString()));
            } else {
                att.setWorkHours(0.0);
            }

            boolean success = attendanceDAO.createAttendance(att);
            if (success) {
                return ResponseEntity.ok("Attendance record created successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to insert attendance record");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving attendance record: " + e.getMessage());
        }
    }

    @PostMapping("/clock-in")
    public ResponseEntity<?> clockIn(@RequestBody Map<String, Object> payload) {
        try {
            int employeeId = Integer.parseInt(payload.get("employeeId").toString());
            String status = payload.containsKey("status") ? payload.get("status").toString() : "Present";

            boolean success = attendanceDAO.recordClockIn(employeeId, status);
            if (success) {
                return ResponseEntity.ok("Clock-in recorded successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to record clock-in");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Clock-in error: " + e.getMessage());
        }
    }

    @PostMapping("/clock-out")
    public ResponseEntity<?> clockOut(@RequestBody Map<String, Object> payload) {
        try {
            int attendanceId = Integer.parseInt(payload.get("attendanceId").toString());
            double workHours = Double.parseDouble(payload.getOrDefault("workHours", 8.0).toString());

            boolean success = attendanceDAO.recordClockOut(attendanceId, workHours);
            if (success) {
                return ResponseEntity.ok("Clock-out recorded successfully");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to record clock-out");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Clock-out error: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttendance(@PathVariable int id, @RequestBody Map<String, Object> payload) {
        try {
            Attendance att = new Attendance();
            att.setAttendanceId(id);
            if (payload.containsKey("employeeId") && payload.get("employeeId") != null) {
                att.setEmployeeId(Integer.parseInt(payload.get("employeeId").toString()));
            }
            att.setClockInTime(payload.get("clockInTime") != null ? payload.get("clockInTime").toString() : null);
            att.setClockOutTime(payload.get("clockOutTime") != null ? payload.get("clockOutTime").toString() : null);
            att.setStatus(payload.get("status") != null ? payload.get("status").toString() : "Present");

            if (payload.containsKey("workHours") && payload.get("workHours") != null) {
                att.setWorkHours(Double.parseDouble(payload.get("workHours").toString()));
            }

            boolean success = attendanceDAO.updateAttendance(att);
            if (success) {
                return ResponseEntity.ok("Attendance record updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attendance record not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating attendance record: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAttendance(@PathVariable int id) {
        try {
            boolean success = attendanceDAO.deleteAttendance(id);
            if (success) {
                return ResponseEntity.ok("Attendance record deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attendance record not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting attendance record: " + e.getMessage());
        }
    }
}