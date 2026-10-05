package com.hotelworkforce.executive.servlet;

import com.hotelworkforce.executive.dao.AnalyticsDAO;
import com.hotelworkforce.executive.dao.ReportDefinitionDAO;
import com.hotelworkforce.executive.model.*;
import com.hotelworkforce.executive.util.SimplePdf;
import com.hotelworkforce.executive.util.WebUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/executive/export")
public class ExportServlet extends HttpServlet {
    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();
    private final ReportDefinitionDAO reportDAO = new ReportDefinitionDAO();
    private final DecimalFormat money = new DecimalFormat("#,##0.00");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String format = req.getParameter("format");
            if (format == null) format = "csv";
            long id = WebUtil.parseLong(req.getParameter("id"), -1);
            String title = "Executive Dashboard";
            AnalyticsFilter filter;
            if (id > 0) {
                ReportDefinition report = reportDAO.findById(id);
                if (report == null) { resp.sendError(404); return; }
                title = report.getName();
                filter = WebUtil.filterFromReport(report);
            } else {
                filter = WebUtil.filterFromRequest(req, true);
            }
            AnalyticsSummary summary = analyticsDAO.loadSummary(filter);
            String baseName = safeFileName(title) + "-" + LocalDate.now();
            if ("pdf".equalsIgnoreCase(format)) {
                exportPdf(resp, baseName, title, filter, summary);
            } else {
                exportCsv(resp, baseName, title, filter, summary);
            }
        } catch (RuntimeException ex) {
            resp.sendError(500, "Unable to export report from TXT storage: " + ex.getMessage());
        }
    }

    private void exportCsv(HttpServletResponse resp, String file, String title, AnalyticsFilter filter, AnalyticsSummary s) throws IOException {
        StringBuilder csv = new StringBuilder();
        csv.append("Report,").append(csv(title)).append("\n");
        csv.append("Department,").append(csv(filter.getDepartment() == null ? "All" : filter.getDepartment())).append("\n");
        csv.append("Role,").append(csv(filter.getRole() == null ? "All" : filter.getRole())).append("\n");
        csv.append("Date From,").append(csv(String.valueOf(filter.getDateFrom()))).append("\n");
        csv.append("Date To,").append(csv(String.valueOf(filter.getDateTo()))).append("\n\n");
        csv.append("Metric,Value\n");
        csv.append("Headcount,").append(s.getHeadcount()).append("\n");
        csv.append("Attendance Rate,").append(String.format("%.2f%%", s.getAttendanceRate())).append("\n");
        csv.append("Absences,").append(s.getAbsenceCount()).append("\n");
        csv.append("Late Records,").append(s.getLateCount()).append("\n");
        csv.append("Approved Leave Requests,").append(s.getApprovedLeaveCount()).append("\n");
        csv.append("Approved Leave Days,").append(String.format("%.2f", s.getApprovedLeaveDays())).append("\n");
        csv.append("Payroll Expenditure,").append(String.format("%.2f", s.getPayrollExpenditure())).append("\n");
        csv.append("Overtime Cost,").append(String.format("%.2f", s.getOvertimeCost())).append("\n");
        csv.append("Deductions,").append(String.format("%.2f", s.getDeductions())).append("\n");
        appendSeries(csv, "Headcount by Department", s.getHeadcountByDepartment());
        appendSeries(csv, "Attendance Trend (%)", s.getAttendanceTrend());
        appendSeries(csv, "Leave Days by Type", s.getLeaveByType());
        appendSeries(csv, "Payroll Trend", s.getPayrollTrend());

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".csv\"");
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }

    private void exportPdf(HttpServletResponse resp, String file, String title, AnalyticsFilter filter, AnalyticsSummary s) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("Department: " + (filter.getDepartment() == null ? "All" : filter.getDepartment()));
        lines.add("Role: " + (filter.getRole() == null ? "All" : filter.getRole()));
        lines.add("Date range: " + filter.getDateFrom() + " to " + filter.getDateTo());
        lines.add("");
        lines.add("KEY METRICS");
        lines.add("Headcount: " + s.getHeadcount());
        lines.add(String.format("Attendance rate: %.2f%%", s.getAttendanceRate()));
        lines.add("Absence records: " + s.getAbsenceCount());
        lines.add("Late records: " + s.getLateCount());
        lines.add("Approved leave requests: " + s.getApprovedLeaveCount());
        lines.add(String.format("Approved leave days: %.2f", s.getApprovedLeaveDays()));
        lines.add("Payroll expenditure: LKR " + money.format(s.getPayrollExpenditure()));
        lines.add("Overtime cost: LKR " + money.format(s.getOvertimeCost()));
        lines.add("Deductions: LKR " + money.format(s.getDeductions()));
        appendSeries(lines, "HEADCOUNT BY DEPARTMENT", s.getHeadcountByDepartment());
        appendSeries(lines, "ATTENDANCE TREND (%)", s.getAttendanceTrend());
        appendSeries(lines, "LEAVE DAYS BY TYPE", s.getLeaveByType());
        appendSeries(lines, "PAYROLL TREND", s.getPayrollTrend());

        byte[] pdf = SimplePdf.create(title, lines);
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + file + ".pdf\"");
        resp.setContentLength(pdf.length);
        resp.getOutputStream().write(pdf);
    }

    private void appendSeries(StringBuilder csv, String title, List<ChartPoint> points) {
        csv.append("\n").append(csv(title)).append("\nLabel,Value\n");
        for (ChartPoint p : points) csv.append(csv(p.getLabel())).append(',').append(p.getValue()).append("\n");
    }

    private void appendSeries(List<String> lines, String title, List<ChartPoint> points) {
        lines.add(""); lines.add(title);
        if (points.isEmpty()) { lines.add("No data available for the selected filter."); return; }
        for (ChartPoint p : points) lines.add(p.getLabel() + ": " + money.format(p.getValue()));
    }

    private String csv(String value) {
        if (value == null || "null".equals(value)) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private String safeFileName(String value) {
        String result = value == null ? "executive-report" : value.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return result.isEmpty() ? "executive-report" : result;
    }
}
