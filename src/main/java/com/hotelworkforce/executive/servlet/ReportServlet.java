package com.hotelworkforce.executive.servlet;

import com.hotelworkforce.executive.dao.AnalyticsDAO;
import com.hotelworkforce.executive.dao.ExecutiveNoteDAO;
import com.hotelworkforce.executive.dao.ReportDefinitionDAO;
import com.hotelworkforce.executive.model.AnalyticsFilter;
import com.hotelworkforce.executive.model.AnalyticsSummary;
import com.hotelworkforce.executive.model.ReportDefinition;
import com.hotelworkforce.executive.util.Json;
import com.hotelworkforce.executive.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/executive/reports/*")
public class ReportServlet extends HttpServlet {
    private final ReportDefinitionDAO reportDAO = new ReportDefinitionDAO();
    private final ExecutiveNoteDAO noteDAO = new ExecutiveNoteDAO();
    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.moveFlashToRequest(req);
        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) { list(req, resp); return; }
        switch (path) {
            case "/create": showForm(req, resp, new ReportDefinition(), false); break;
            case "/edit": showEdit(req, resp); break;
            case "/view": view(req, resp); break;
            default: resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/create".equals(path)) { create(req, resp); return; }
        if ("/edit".equals(path)) { update(req, resp); return; }
        if ("/delete".equals(path)) { delete(req, resp); return; }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = WebUtil.clean(req.getParameter("q"), 100);
        req.setAttribute("reports", reportDAO.findAll(search));
        req.setAttribute("search", search);
        req.setAttribute("pageTitle", "Saved Executive Reports");
        req.getRequestDispatcher("/WEB-INF/views/reports.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = WebUtil.parseLong(req.getParameter("id"), -1);
        ReportDefinition report = reportDAO.findById(id);
        if (report == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        showForm(req, resp, report, true);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, ReportDefinition report, boolean edit) throws ServletException, IOException {
        if (!edit) {
            report.setDepartment(WebUtil.clean(req.getParameter("department"), 80));
            report.setRoleFilter(WebUtil.clean(req.getParameter("role"), 80));
            report.setDateFrom(WebUtil.parseDate(req.getParameter("dateFrom")));
            report.setDateTo(WebUtil.parseDate(req.getParameter("dateTo")));
            report.setReportType("WORKFORCE");
            report.setStatus("ACTIVE");
        }
        req.setAttribute("report", report);
        req.setAttribute("editMode", edit);
        req.setAttribute("departments", analyticsDAO.listDepartments());
        req.setAttribute("roles", analyticsDAO.listRoles());
        req.setAttribute("pageTitle", edit ? "Edit Executive Report" : "Create Executive Report");
        req.getRequestDispatcher("/WEB-INF/views/report-form.jsp").forward(req, resp);
    }

    private void create(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReportDefinition report = readReport(req, false);
        String error = validate(report);
        if (error != null) {
            req.setAttribute("formError", error);
            showForm(req, resp, report, false);
            return;
        }
        Object name = req.getSession().getAttribute("displayName");
        report.setCreatedBy(name == null ? "Director" : String.valueOf(name));
        long id = reportDAO.create(report);
        WebUtil.flash(req, "success", "Executive report created successfully and saved to TXT storage.");
        resp.sendRedirect(req.getContextPath() + "/executive/reports/view?id=" + id);
    }

    private void update(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ReportDefinition report = readReport(req, true);
        if (reportDAO.findById(report.getId()) == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        String error = validate(report);
        if (error != null) {
            req.setAttribute("formError", error);
            showForm(req, resp, report, true);
            return;
        }
        reportDAO.update(report);
        WebUtil.flash(req, "success", "Executive report updated in TXT storage.");
        resp.sendRedirect(req.getContextPath() + "/executive/reports/view?id=" + report.getId());
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = WebUtil.parseLong(req.getParameter("id"), -1);
        boolean deleted = reportDAO.delete(id);
        WebUtil.flash(req, deleted ? "success" : "warning", deleted ? "Executive report deleted from TXT storage." : "Report was not found.");
        resp.sendRedirect(req.getContextPath() + "/executive/reports/");
    }

    private void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long id = WebUtil.parseLong(req.getParameter("id"), -1);
        ReportDefinition report = reportDAO.findById(id);
        if (report == null) { resp.sendError(HttpServletResponse.SC_NOT_FOUND); return; }
        AnalyticsFilter filter = WebUtil.filterFromReport(report);
        AnalyticsSummary summary = analyticsDAO.loadSummary(filter);
        req.setAttribute("report", report);
        req.setAttribute("filter", filter);
        req.setAttribute("summary", summary);
        req.setAttribute("notes", noteDAO.findByReportId(id));
        req.setAttribute("headcountJson", Json.chartPoints(summary.getHeadcountByDepartment()));
        req.setAttribute("attendanceJson", Json.chartPoints(summary.getAttendanceTrend()));
        req.setAttribute("leaveJson", Json.chartPoints(summary.getLeaveByType()));
        req.setAttribute("payrollJson", Json.chartPoints(summary.getPayrollTrend()));
        req.setAttribute("pageTitle", report.getName());
        req.getRequestDispatcher("/WEB-INF/views/report-view.jsp").forward(req, resp);
    }

    private ReportDefinition readReport(HttpServletRequest req, boolean withId) {
        ReportDefinition r = new ReportDefinition();
        if (withId) r.setId(WebUtil.parseLong(req.getParameter("id"), -1));
        r.setName(WebUtil.clean(req.getParameter("name"), 140));
        r.setDescription(WebUtil.clean(req.getParameter("description"), 1200));
        r.setReportType(WebUtil.reportType(req.getParameter("reportType")));
        r.setDepartment(WebUtil.clean(req.getParameter("department"), 80));
        r.setRoleFilter(WebUtil.clean(req.getParameter("role"), 80));
        r.setDateFrom(WebUtil.parseDate(req.getParameter("dateFrom")));
        r.setDateTo(WebUtil.parseDate(req.getParameter("dateTo")));
        r.setStatus(WebUtil.reportStatus(req.getParameter("status")));
        return r;
    }

    private String validate(ReportDefinition report) {
        if (report.getName() == null || report.getName().length() < 3) return "Report name must contain at least 3 characters.";
        if (report.getDateFrom() != null && report.getDateTo() != null && report.getDateFrom().isAfter(report.getDateTo())) return "Start date cannot be after end date.";
        return null;
    }
}
