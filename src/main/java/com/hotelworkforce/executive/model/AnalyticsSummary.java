package com.hotelworkforce.executive.model;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsSummary {
    private int headcount;
    private double attendanceRate;
    private int absenceCount;
    private int lateCount;
    private int approvedLeaveCount;
    private double approvedLeaveDays;
    private double payrollExpenditure;
    private double overtimeCost;
    private double deductions;
    private final List<ChartPoint> headcountByDepartment = new ArrayList<>();
    private final List<ChartPoint> attendanceTrend = new ArrayList<>();
    private final List<ChartPoint> leaveByType = new ArrayList<>();
    private final List<ChartPoint> payrollTrend = new ArrayList<>();

    public int getHeadcount() { return headcount; }
    public void setHeadcount(int headcount) { this.headcount = headcount; }
    public double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(double attendanceRate) { this.attendanceRate = attendanceRate; }
    public int getAbsenceCount() { return absenceCount; }
    public void setAbsenceCount(int absenceCount) { this.absenceCount = absenceCount; }
    public int getLateCount() { return lateCount; }
    public void setLateCount(int lateCount) { this.lateCount = lateCount; }
    public int getApprovedLeaveCount() { return approvedLeaveCount; }
    public void setApprovedLeaveCount(int approvedLeaveCount) { this.approvedLeaveCount = approvedLeaveCount; }
    public double getApprovedLeaveDays() { return approvedLeaveDays; }
    public void setApprovedLeaveDays(double approvedLeaveDays) { this.approvedLeaveDays = approvedLeaveDays; }
    public double getPayrollExpenditure() { return payrollExpenditure; }
    public void setPayrollExpenditure(double payrollExpenditure) { this.payrollExpenditure = payrollExpenditure; }
    public double getOvertimeCost() { return overtimeCost; }
    public void setOvertimeCost(double overtimeCost) { this.overtimeCost = overtimeCost; }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public List<ChartPoint> getHeadcountByDepartment() { return headcountByDepartment; }
    public List<ChartPoint> getAttendanceTrend() { return attendanceTrend; }
    public List<ChartPoint> getLeaveByType() { return leaveByType; }
    public List<ChartPoint> getPayrollTrend() { return payrollTrend; }
}
