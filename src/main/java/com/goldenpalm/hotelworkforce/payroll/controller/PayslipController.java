package com.goldenpalm.hotelworkforce.payroll.controller;

import com.goldenpalm.hotelworkforce.payroll.entity.Payroll;
import com.goldenpalm.hotelworkforce.payroll.entity.PayrollStatus;
import com.goldenpalm.hotelworkforce.payroll.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

/**
 * Kept separate from PayrollController because payslips are also pulled by staff
 * from their own self-service profile (Section 7, "Profile View" — pay slip archive),
 * not just by Finance.
 */
@RestController
@RequestMapping("/api/payslips")
public class PayslipController {

    private final PayrollRepository payrollRepository;

    @Autowired
    public PayslipController(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    /**
     * GET /api/payslips/{payrollId}
     * Returns the payslip as a downloadable file — the "Payslip" button per row in the UI.
     * NOTE: this returns a plain-text payslip to keep the demo dependency-free. Swap the
     * body/content-type for a real PDF renderer (e.g. Apache PDFBox or iText) to match the
     * proposal's "downloadable PDF payslip" requirement (Section 6, Module 5).
     */
    @GetMapping("/{payrollId}")
    @PreAuthorize("hasAnyRole('FINANCE', 'STAFF')")
    public ResponseEntity<byte[]> downloadPayslip(@PathVariable Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new NoSuchElementException("Payroll record not found: " + payrollId));

        if (payroll.getStatus() != PayrollStatus.APPROVED) {
            String message = "Payslip is not available until payroll is approved.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(message.getBytes(StandardCharsets.UTF_8));
        }

        byte[] body = buildPayslipText(payroll).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=payslip-" + payroll.getEmployee().getId() + "-" + payroll.getPayPeriod() + ".txt");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }

    private String buildPayslipText(Payroll p) {
        return "GOLDEN PALM HOTEL — PAYSLIP\n" +
                "--------------------------------\n" +
                "Employee   : " + p.getEmployee().getFullName() + "\n" +
                "Department : " + p.getEmployee().getDepartment() + "\n" +
                "Pay period : " + p.getPayPeriod() + "\n" +
                "--------------------------------\n" +
                "Base pay    : " + p.getBasePay() + "\n" +
                "Overtime    : " + p.getOvertimePay() + "\n" +
                "Bonus       : " + p.getBonus() + "\n" +
                "Deductions  : -" + p.getDeductions() + "\n" +
                "--------------------------------\n" +
                "NET PAY     : " + p.getNetPay() + "\n" +
                "Status      : " + p.getStatus() + "\n";
    }
}
