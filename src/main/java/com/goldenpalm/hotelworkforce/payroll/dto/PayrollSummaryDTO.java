package com.goldenpalm.hotelworkforce.payroll.dto;

/**
 * Backs the four stat cards at the top of the Payroll & Payslip Processing screen:
 * Total payroll, Overtime, Pending approval, Payslips generated.
 */
public class PayrollSummaryDTO {

    private Double totalPayroll;
    private Double totalOvertimePay;
    private long pendingCount;
    private long approvedCount;
    private long totalEmployees;

    public PayrollSummaryDTO() {
    }

    public PayrollSummaryDTO(Double totalPayroll, Double totalOvertimePay, long pendingCount,
                              long approvedCount, long totalEmployees) {
        this.totalPayroll = totalPayroll;
        this.totalOvertimePay = totalOvertimePay;
        this.pendingCount = pendingCount;
        this.approvedCount = approvedCount;
        this.totalEmployees = totalEmployees;
    }

    public Double getTotalPayroll() {
        return totalPayroll;
    }

    public void setTotalPayroll(Double totalPayroll) {
        this.totalPayroll = totalPayroll;
    }

    public Double getTotalOvertimePay() {
        return totalOvertimePay;
    }

    public void setTotalOvertimePay(Double totalOvertimePay) {
        this.totalOvertimePay = totalOvertimePay;
    }

    public long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public long getApprovedCount() {
        return approvedCount;
    }

    public void setApprovedCount(long approvedCount) {
        this.approvedCount = approvedCount;
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }
}
