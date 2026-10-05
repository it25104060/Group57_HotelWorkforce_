package org.example.seproject1.model;

import java.time.LocalDate;

public class LeaveRequest {
    private Integer leaveID;
    private Integer employeeID;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer durationDays;
    private String reason;
    private String status;

    // Default No-Arg Constructor (Required for Spring / Jackson JSON Deserialization)
    public LeaveRequest() {}

    // Full Parameterized Constructor
    public LeaveRequest(Integer leaveID, Integer employeeID, String leaveType, LocalDate startDate, LocalDate endDate, Integer durationDays, String reason, String status) {
        this.leaveID = leaveID;
        this.employeeID = employeeID;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.durationDays = durationDays;
        this.reason = reason;
        this.status = status;
    }

    // Getters and Setters
    public Integer getLeaveID() { return leaveID; }
    public void setLeaveID(Integer leaveID) { this.leaveID = leaveID; }

    public Integer getEmployeeID() { return employeeID; }
    public void setEmployeeID(Integer employeeID) { this.employeeID = employeeID; }

    public String getLeaveType() { return leaveType; }
    public void setLeaveType(String leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Integer getDurationDays() { return durationDays; }
    public void setDurationDays(Integer durationDays) { this.durationDays = durationDays; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}