package com.hotelworkforce.executive.dao;

import com.hotelworkforce.executive.config.StorageManager;
import com.hotelworkforce.executive.model.AnalyticsFilter;
import com.hotelworkforce.executive.model.AnalyticsSummary;
import com.hotelworkforce.executive.model.ChartPoint;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

/**
 * Analytics computed directly from TXT storage files.
 */
public class AnalyticsDAO {

    public AnalyticsSummary loadSummary(AnalyticsFilter filter) {
        synchronized (StorageManager.LOCK) {
            Map<Long, Employee> employees = loadEmployees();
            AnalyticsSummary summary = new AnalyticsSummary();

            List<Employee> selectedEmployees = new ArrayList<>();
            for (Employee e : employees.values()) {
                if (e.active && matchesEmployee(e, filter)) selectedEmployees.add(e);
            }
            summary.setHeadcount(selectedEmployees.size());

            Set<Long> allowedIds = new HashSet<>();
            for (Employee e : selectedEmployees) allowedIds.add(e.id);

            loadAttendance(filter, allowedIds, summary);
            loadLeave(filter, allowedIds, summary);
            loadPayroll(filter, allowedIds, summary);
            loadHeadcountChart(selectedEmployees, summary);
            return summary;
        }
    }

    public List<String> listDepartments() {
        synchronized (StorageManager.LOCK) {
            TreeSet<String> values = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Employee e : loadEmployees().values()) if (e.active && e.department != null) values.add(e.department);
            return new ArrayList<>(values);
        }
    }

    public List<String> listRoles() {
        synchronized (StorageManager.LOCK) {
            TreeSet<String> values = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (Employee e : loadEmployees().values()) if (e.active && e.role != null) values.add(e.role);
            return new ArrayList<>(values);
        }
    }

    private void loadAttendance(AnalyticsFilter f, Set<Long> allowedIds, AnalyticsSummary s) {
        int total = 0, present = 0, absent = 0, late = 0;
        Map<YearMonth, int[]> monthly = new TreeMap<>();

        for (String line : StorageManager.readDataLines("attendance_records.txt")) {
            List<String> p = StorageManager.split(line);
            long employeeId = StorageManager.longValue(StorageManager.value(p, 1), -1);
            LocalDate date = parseDate(StorageManager.value(p, 2));
            String status = upper(StorageManager.value(p, 3));
            int lateMinutes = StorageManager.intValue(StorageManager.value(p, 4), 0);
            if (!allowedIds.contains(employeeId) || !within(date, f.getDateFrom(), f.getDateTo())) continue;

            total++;
            if ("PRESENT".equals(status) || "LATE".equals(status)) present++;
            if ("ABSENT".equals(status)) absent++;
            if ("LATE".equals(status) || lateMinutes > 10) late++;
            if (date != null) {
                int[] counts = monthly.computeIfAbsent(YearMonth.from(date), k -> new int[2]);
                counts[0]++;
                if ("PRESENT".equals(status) || "LATE".equals(status)) counts[1]++;
            }
        }
        s.setAttendanceRate(total == 0 ? 0 : present * 100.0 / total);
        s.setAbsenceCount(absent);
        s.setLateCount(late);
        for (Map.Entry<YearMonth, int[]> e : monthly.entrySet()) {
            int[] c = e.getValue();
            s.getAttendanceTrend().add(new ChartPoint(e.getKey().toString(), c[0] == 0 ? 0 : c[1] * 100.0 / c[0]));
        }
    }

    private void loadLeave(AnalyticsFilter f, Set<Long> allowedIds, AnalyticsSummary s) {
        int count = 0;
        double days = 0;
        Map<String, Double> byType = new HashMap<>();
        for (String line : StorageManager.readDataLines("leave_requests.txt")) {
            List<String> p = StorageManager.split(line);
            long employeeId = StorageManager.longValue(StorageManager.value(p, 1), -1);
            String leaveType = defaultText(StorageManager.value(p, 2), "Other");
            LocalDate start = parseDate(StorageManager.value(p, 3));
            LocalDate end = parseDate(StorageManager.value(p, 4));
            double dayCount = StorageManager.doubleValue(StorageManager.value(p, 5), 0);
            String status = upper(StorageManager.value(p, 6));
            if (!allowedIds.contains(employeeId) || !"APPROVED".equals(status) || !overlaps(start, end, f.getDateFrom(), f.getDateTo())) continue;
            count++;
            days += dayCount;
            byType.merge(leaveType, dayCount, Double::sum);
        }
        s.setApprovedLeaveCount(count);
        s.setApprovedLeaveDays(days);
        byType.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(e -> s.getLeaveByType().add(new ChartPoint(e.getKey(), e.getValue())));
    }

    private void loadPayroll(AnalyticsFilter f, Set<Long> allowedIds, AnalyticsSummary s) {
        double net = 0, overtime = 0, deductions = 0;
        Map<YearMonth, Double> monthly = new TreeMap<>();
        for (String line : StorageManager.readDataLines("payroll_records.txt")) {
            List<String> p = StorageManager.split(line);
            long employeeId = StorageManager.longValue(StorageManager.value(p, 1), -1);
            LocalDate payPeriod = parseDate(StorageManager.value(p, 2));
            double rowNet = StorageManager.doubleValue(StorageManager.value(p, 3), 0);
            double rowOvertime = StorageManager.doubleValue(StorageManager.value(p, 4), 0);
            double rowDeduction = StorageManager.doubleValue(StorageManager.value(p, 5), 0);
            String status = upper(StorageManager.value(p, 6));
            if (!allowedIds.contains(employeeId) || !"APPROVED".equals(status) || !within(payPeriod, f.getDateFrom(), f.getDateTo())) continue;
            net += rowNet;
            overtime += rowOvertime;
            deductions += rowDeduction;
            if (payPeriod != null) monthly.merge(YearMonth.from(payPeriod), rowNet, Double::sum);
        }
        s.setPayrollExpenditure(net);
        s.setOvertimeCost(overtime);
        s.setDeductions(deductions);
        for (Map.Entry<YearMonth, Double> e : monthly.entrySet()) {
            s.getPayrollTrend().add(new ChartPoint(e.getKey().toString(), e.getValue()));
        }
    }

    private void loadHeadcountChart(List<Employee> employees, AnalyticsSummary s) {
        Map<String, Integer> counts = new HashMap<>();
        for (Employee e : employees) counts.merge(defaultText(e.department, "Unassigned"), 1, Integer::sum);
        counts.entrySet().stream()
                .sorted((a, b) -> {
                    int byCount = Integer.compare(b.getValue(), a.getValue());
                    return byCount != 0 ? byCount : a.getKey().compareToIgnoreCase(b.getKey());
                })
                .forEach(e -> s.getHeadcountByDepartment().add(new ChartPoint(e.getKey(), e.getValue())));
    }

    private Map<Long, Employee> loadEmployees() {
        Map<Long, Employee> map = new LinkedHashMap<>();
        for (String line : StorageManager.readDataLines("employees.txt")) {
            List<String> p = StorageManager.split(line);
            Employee e = new Employee();
            e.id = StorageManager.longValue(StorageManager.value(p, 0), -1);
            e.name = StorageManager.value(p, 1);
            e.department = StorageManager.value(p, 2);
            e.role = StorageManager.value(p, 3);
            e.active = StorageManager.intValue(StorageManager.value(p, 4), 0) == 1;
            if (e.id > 0) map.put(e.id, e);
        }
        return map;
    }

    private boolean matchesEmployee(Employee e, AnalyticsFilter f) {
        if (f.getDepartment() != null && !f.getDepartment().equalsIgnoreCase(defaultText(e.department, ""))) return false;
        if (f.getRole() != null && !f.getRole().equalsIgnoreCase(defaultText(e.role, ""))) return false;
        return true;
    }

    private boolean within(LocalDate value, LocalDate from, LocalDate to) {
        if (value == null) return false;
        if (from != null && value.isBefore(from)) return false;
        if (to != null && value.isAfter(to)) return false;
        return true;
    }

    private boolean overlaps(LocalDate start, LocalDate end, LocalDate from, LocalDate to) {
        if (start == null || end == null) return false;
        if (from != null && end.isBefore(from)) return false;
        if (to != null && start.isAfter(to)) return false;
        return true;
    }

    private LocalDate parseDate(String v) {
        try { return v == null ? null : LocalDate.parse(v); } catch (Exception ex) { return null; }
    }

    private String upper(String v) { return v == null ? "" : v.trim().toUpperCase(Locale.ROOT); }
    private String defaultText(String v, String d) { return v == null || v.trim().isEmpty() ? d : v.trim(); }

    private static class Employee {
        long id;
        String name;
        String department;
        String role;
        boolean active;
    }
}
