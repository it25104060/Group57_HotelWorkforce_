package org.example.seproject1.controller;

import jakarta.servlet.http.HttpSession;
import org.example.seproject1.dao.EmployeeDAO;
import org.example.seproject1.model.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private int getCurrentUserId(HttpSession session) {
        if (session != null && session.getAttribute("userId") != null) {
            return (Integer) session.getAttribute("userId");
        }
        return 1;
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            List<Employee> employees = employeeDAO.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<String> addEmployee(@RequestBody Employee employee, HttpSession session) {
        try {
            boolean success = employeeDAO.addEmployee(employee);
            if (success) {
                int userId = getCurrentUserId(session);
                employeeDAO.logAction(userId, "HR Manager created staff profile for " + employee.getFullName() + " (NIC: " + employee.getNic() + ")");
                return ResponseEntity.status(HttpStatus.CREATED).body("Employee created successfully.");
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create employee.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable int id, @RequestBody Employee employee, HttpSession session) {
        try {
            employee.setEmployeeId(id);
            boolean success = employeeDAO.updateEmployee(employee);
            if (success) {
                int userId = getCurrentUserId(session);
                employeeDAO.logAction(userId, "HR Manager updated staff profile for ID " + id + " (Status: " + employee.getStatus() + ")");
                return ResponseEntity.ok("Employee updated successfully.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee record not found.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable int id, HttpSession session) {
        try {
            boolean success = employeeDAO.deleteEmployee(id);
            if (success) {
                int userId = getCurrentUserId(session);
                employeeDAO.logAction(userId, "HR Manager deleted staff profile ID " + id);
                return ResponseEntity.ok("Employee deleted successfully.");
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Employee record not found.");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error: " + e.getMessage());
        }
    }
}