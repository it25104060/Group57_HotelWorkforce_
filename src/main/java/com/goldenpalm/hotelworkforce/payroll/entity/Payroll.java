package com.goldenpalm.hotelworkforce.payroll.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * One payroll record = one employee's pay for one pay period.
 * Matches the "Payroll register" table in the UI: base pay, overtime, bonus,
 * deductions, net pay, and approval status.
 */
@Entity
@Table(
        name = "payroll_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "pay_period"})
)
public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    /** Format: "yyyy-MM", e.g. "2026-08" — matches the period dropdown in the UI. */
    @Column(name = "pay_period", nullable = false)
    private String payPeriod;

    @Column(name = "base_pay", nullable = false)
    private Double basePay = 0.0;

    @Column(name = "overtime_pay", nullable = false)
    private Double overtimePay = 0.0;

    @Column(nullable = false)
    private Double bonus = 0.0;

    @Column(nullable = false)
    private Double deductions = 0.0;

    @Column(name = "net_pay", nullable = false)
    private Double netPay = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status = PayrollStatus.PENDING;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Payroll() {
    }

    public Payroll(Employee employee, String payPeriod, Double basePay, Double overtimePay,
                    Double bonus, Double deductions) {
        this.employee = employee;
        this.payPeriod = payPeriod;
        this.basePay = basePay;
        this.overtimePay = overtimePay;
        this.bonus = bonus;
        this.deductions = deductions;
        recalculateNetPay();
    }

    /**
     * Proposal rule (Section 6, Module 5): net pay = base pay + overtime + bonus - deductions.
     * The proposal also states payroll recalculates automatically whenever attendance or
     * leave records change, as long as the record is still pending — call this again
     * before approval if any input changed.
     */
    public void recalculateNetPay() {
        double b = basePay == null ? 0 : basePay;
        double o = overtimePay == null ? 0 : overtimePay;
        double bo = bonus == null ? 0 : bonus;
        double d = deductions == null ? 0 : deductions;
        this.netPay = b + o + bo - d;
    }

    /** Locks the record and stamps the approval time — matches "Approve batch" in the UI. */
    public void approve() {
        this.status = PayrollStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getPayPeriod() {
        return payPeriod;
    }

    public void setPayPeriod(String payPeriod) {
        this.payPeriod = payPeriod;
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

    public PayrollStatus getStatus() {
        return status;
    }

    public void setStatus(PayrollStatus status) {
        this.status = status;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
