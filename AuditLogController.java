package org.example.seproject1.controller;

import org.example.seproject1.dao.AuditLogDAO;
import org.example.seproject1.model.AuditLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@CrossOrigin(origins = "*")
public class AuditLogController {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @GetMapping
    public ResponseEntity<?> getAuditLogs() {
        try {
            List<AuditLog> logs = auditLogDAO.getAllLogs();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error fetching audit logs: " + e.getMessage());
        }
    }
}