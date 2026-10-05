package com.goldenpalm.hotelworkforce.payroll.dto;

/**
 * One row of the "Payroll register" table in the UI.
 * Kept separate from the Payroll entity so the API never leaks JPA internals
 * (lazy proxies, entity relations) straight to the frontend.
 */
public class PayrollRowDTO {

    private Long payrollId;
    private Long employeeId;
    private String employeeName;
    private String department;
    private Double basePay;
    private Double overtimePay;
    private Double bonus;
    private Double deductions;
    private Double netPay;
    private String status; // "PENDING" | "APPROVED"

    public PayrollRowDTO() {
    }

    public PayrollRowDTO(Long payrollId, Long employeeId, String employeeName, String department,
                          Double basePay, Double overtimePay, Double bonus, Double deductions,
                          Double netPay, String status) {
        this.payrollId = payrollId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.basePay = basePay;
        this.overtimePay = overtimePay;
        this.bonus = bonus;
        this.deductions = deductions;
        this.netPay = netPay;
        this.status = status;
    }

    public Long getPayrollId() {
        return payrollId;
    }

    public void setPayrollId(Long payrollId) {
        this.payrollId = payrollId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Double getBasePay() {
        return basePay;
    }

    public void setBasePay(Double basePay) {
        this.basePay = basePay;
    }

    public Double getOvertimePay() {
        return overtimePay;
    }

    public void setOvertimePay(Double overtimePay) {
        this.overtimePay = overtimePay;
    }

    public Double getBonus() {
        return bonus;
    }

    public void setBonus(Double bonus) {
        this.bonus = bonus;
    }

    public Double getDeductions() {
        return deductions;
    }

    public void setDeductions(Double deductions) {
        this.deductions = deductions;
    }

    public Double getNetPay() {
        return netPay;
    }

    public void setNetPay(Double netPay) {
        this.netPay = netPay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
