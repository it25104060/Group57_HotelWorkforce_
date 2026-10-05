package com.hotelworkforce.executive.servlet;

import com.hotelworkforce.executive.config.StorageException;
import com.hotelworkforce.executive.dao.AnalyticsDAO;
import com.hotelworkforce.executive.dao.ExecutiveNoteDAO;
import com.hotelworkforce.executive.dao.ReportDefinitionDAO;
import com.hotelworkforce.executive.model.AnalyticsFilter;
import com.hotelworkforce.executive.model.AnalyticsSummary;
import com.hotelworkforce.executive.util.Json;
import com.hotelworkforce.executive.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/executive/dashboard")
public class DashboardServlet extends HttpServlet {
    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();
    private final ReportDefinitionDAO reportDAO = new ReportDefinitionDAO();
    private final ExecutiveNoteDAO noteDAO = new ExecutiveNoteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.moveFlashToRequest(req);
        try {
            AnalyticsFilter filter = WebUtil.filterFromRequest(req, true);
            AnalyticsSummary summary = analyticsDAO.loadSummary(filter);
            req.setAttribute("filter", filter);
            req.setAttribute("summary", summary);
            req.setAttribute("departments", analyticsDAO.listDepartments());
            req.setAttribute("roles", analyticsDAO.listRoles());
            req.setAttribute("reportCount", reportDAO.count());
            req.setAttribute("noteCount", noteDAO.count());
            req.setAttribute("headcountJson", Json.chartPoints(summary.getHeadcountByDepartment()));
            req.setAttribute("attendanceJson", Json.chartPoints(summary.getAttendanceTrend()));
            req.setAttribute("leaveJson", Json.chartPoints(summary.getLeaveByType()));
            req.setAttribute("payrollJson", Json.chartPoints(summary.getPayrollTrend()));
            req.setAttribute("pageTitle", "Executive Dashboard");
            req.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(req, resp);
        } catch (StorageException ex) {
            req.setAttribute("pageTitle", "Storage Error");
            req.setAttribute("errorMessage", "Unable to load executive analytics from the TXT storage files. Details: " + ex.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/error-500.jsp").forward(req, resp);
        }
    }
}
