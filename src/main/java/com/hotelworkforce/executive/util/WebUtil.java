package com.hotelworkforce.executive.util;

import com.hotelworkforce.executive.model.AnalyticsFilter;
import com.hotelworkforce.executive.model.ReportDefinition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

public final class WebUtil {
    private static final Set<String> REPORT_TYPES = Set.of("WORKFORCE", "HEADCOUNT", "ATTENDANCE", "LEAVE", "PAYROLL");
    private static final Set<String> REPORT_STATUSES = Set.of("DRAFT", "ACTIVE", "ARCHIVED");
    private static final Set<String> PRIORITIES = Set.of("LOW", "NORMAL", "HIGH", "CRITICAL");

    private WebUtil() {}

    public static AnalyticsFilter filterFromRequest(HttpServletRequest req, boolean withDefaultDates) {
        AnalyticsFilter filter = new AnalyticsFilter();
        filter.setDepartment(req.getParameter("department"));
        filter.setRole(req.getParameter("role"));
        LocalDate from = parseDate(req.getParameter("dateFrom"));
        LocalDate to = parseDate(req.getParameter("dateTo"));
        if (withDefaultDates && from == null && to == null) {
            to = LocalDate.now();
            from = to.minusDays(90);
        }
        filter.setDateFrom(from);
        filter.setDateTo(to);
        return filter;
    }

    public static AnalyticsFilter filterFromReport(ReportDefinition report) {
        AnalyticsFilter filter = new AnalyticsFilter();
        filter.setDepartment(report.getDepartment());
        filter.setRole(report.getRoleFilter());
        filter.setDateFrom(report.getDateFrom());
        filter.setDateTo(report.getDateTo());
        return filter;
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try { return LocalDate.parse(value.trim()); }
        catch (DateTimeParseException ex) { return null; }
    }

    public static long parseLong(String value, long fallback) {
        try { return Long.parseLong(value); }
        catch (Exception ex) { return fallback; }
    }

    public static String clean(String value, int max) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null;
        return v.length() > max ? v.substring(0, max) : v;
    }

    public static String reportType(String value) {
        String v = value == null ? "WORKFORCE" : value.trim().toUpperCase();
        return REPORT_TYPES.contains(v) ? v : "WORKFORCE";
    }

    public static String reportStatus(String value) {
        String v = value == null ? "ACTIVE" : value.trim().toUpperCase();
        return REPORT_STATUSES.contains(v) ? v : "ACTIVE";
    }

    public static String priority(String value) {
        String v = value == null ? "NORMAL" : value.trim().toUpperCase();
        return PRIORITIES.contains(v) ? v : "NORMAL";
    }

    public static void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMessage", message);
    }

    public static void moveFlashToRequest(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;
        Object type = session.getAttribute("flashType");
        Object message = session.getAttribute("flashMessage");
        if (message != null) {
            req.setAttribute("flashType", type == null ? "success" : type);
            req.setAttribute("flashMessage", message);
            session.removeAttribute("flashType");
            session.removeAttribute("flashMessage");
        }
    }
}
