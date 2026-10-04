package org.example.seproject1.model;

public class Attendance {
    private int attendanceId;
    private int employeeId;
    private String employeeName;
    private String date;
    private String clockInTime;
    private String clockOutTime;
    private Double workHours;
    private String status;

    public Attendance() {}

    public Attendance(int attendanceId, int employeeId, String employeeName, String date, String clockInTime, String clockOutTime, Double workHours, String status) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.date = date;
        this.clockInTime = clockInTime;
        this.clockOutTime = clockOutTime;
        this.workHours = workHours;
        this.status = status;
    }

    public int getAttendanceId() { return attendanceId; }
    public void setAttendanceId(int attendanceId) { this.attendanceId = attendanceId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getClockInTime() { return clockInTime; }
    public void setClockInTime(String clockInTime) { this.clockInTime = clockInTime; }

    public String getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(String clockOutTime) { this.clockOutTime = clockOutTime; }

    public Double getWorkHours() { return workHours; }
    public void setWorkHours(Double workHours) { this.workHours = workHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}