package com.hotelworkforce.executive.model;

import java.time.LocalDate;

public class AnalyticsFilter {
    private String department;
    private String role;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = normalize(department); }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = normalize(role); }
    public LocalDate getDateFrom() { return dateFrom; }
    public void setDateFrom(LocalDate dateFrom) { this.dateFrom = dateFrom; }
    public LocalDate getDateTo() { return dateTo; }
    public void setDateTo(LocalDate dateTo) { this.dateTo = dateTo; }

    private String normalize(String value) {
        if (value == null || value.trim().isEmpty() || "ALL".equalsIgnoreCase(value)) return null;
        return value.trim();
    }
}
