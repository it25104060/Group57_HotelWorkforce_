package com.goldenpalm.hotelworkforce.payroll.service;

import com.goldenpalm.hotelworkforce.payroll.dto.PayrollRowDTO;
import com.goldenpalm.hotelworkforce.payroll.dto.PayrollSummaryDTO;

import java.util.List;

/**
 * Business logic for Module 5 (Payroll & Payslip Processing).
 * The controller only talks to this interface — never touches the repository directly.
 */
public interface PayrollService {

    List<PayrollRowDTO> getPayrollForPeriod(String payPeriod);

    PayrollSummaryDTO getSummary(String payPeriod);

    PayrollRowDTO approvePayroll(Long payrollId);

    List<PayrollRowDTO> approveAllPending(String payPeriod);

    String exportPeriodAsCsv(String payPeriod);
}
