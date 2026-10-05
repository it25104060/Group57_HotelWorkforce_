package com.goldenpalm.hotelworkforce.payroll.controller;

import com.goldenpalm.hotelworkforce.payroll.dto.PayrollRowDTO;
import com.goldenpalm.hotelworkforce.payroll.dto.PayrollSummaryDTO;
import com.goldenpalm.hotelworkforce.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints behind the "Payroll & Payslip Processing" screen (Module 5).
 * Restricted to the FINANCE role, per the RBAC requirement in Section 4 of the proposal —
 * "the system shall restrict every screen and action to the permissions defined for the
 * logged-in user's role."
 */
@RestController
@RequestMapping("/api/payroll")
@PreAuthorize("hasRole('FINANCE')")
public class PayrollController {

    private final PayrollService payrollService;

    @Autowired
    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    /** GET /api/payroll?period=2026-08 — fills the payroll register table. */
    @GetMapping
    public ResponseEntity<List<PayrollRowDTO>> getPayroll(@RequestParam String period) {
        return ResponseEntity.ok(payrollService.getPayrollForPeriod(period));
    }

    /** GET /api/payroll/summary?period=2026-08 — fills the four stat cards. */
    @GetMapping("/summary")
    public ResponseEntity<PayrollSummaryDTO> getSummary(@RequestParam String period) {
        return ResponseEntity.ok(payrollService.getSummary(period));
    }

    /** POST /api/payroll/{id}/approve — approve a single employee's row. */
    @PostMapping("/{id}/approve")
    public ResponseEntity<PayrollRowDTO> approveOne(@PathVariable Long id) {
        return ResponseEntity.ok(payrollService.approvePayroll(id));
    }

    /** POST /api/payroll/approve-all?period=2026-08 — the "Approve batch & generate payslips" button. */
    @PostMapping("/approve-all")
    public ResponseEntity<List<PayrollRowDTO>> approveAll(@RequestParam String period) {
        return ResponseEntity.ok(payrollService.approveAllPending(period));
    }

    /** GET /api/payroll/export?period=2026-08 — the "Export CSV" button. */
    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(@RequestParam String period) {
        String csv = payrollService.exportPeriodAsCsv(period);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=payroll-" + period + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
