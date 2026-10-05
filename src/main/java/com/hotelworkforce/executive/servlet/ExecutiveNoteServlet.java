package com.hotelworkforce.executive.servlet;

import com.hotelworkforce.executive.dao.ExecutiveNoteDAO;
import com.hotelworkforce.executive.dao.ReportDefinitionDAO;
import com.hotelworkforce.executive.model.ExecutiveNote;
import com.hotelworkforce.executive.util.WebUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/executive/notes/*")
public class ExecutiveNoteServlet extends HttpServlet {
    private final ExecutiveNoteDAO noteDAO = new ExecutiveNoteDAO();
    private final ReportDefinitionDAO reportDAO = new ReportDefinitionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        WebUtil.moveFlashToRequest(req);
        String path = req.getPathInfo();
        if (path == null || "/".equals(path)) { list(req, resp); return; }
        if ("/create".equals(path)) { showForm(req, resp, new ExecutiveNote(), false); return; }
        if ("/edit".equals(path)) {
            ExecutiveNote note = noteDAO.findById(WebUtil.parseLong(req.getParameter("id"), -1));
            if (note == null) { resp.sendError(404); return; }
            showForm(req, resp, note, true); return;
        }
        resp.sendError(404);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        if ("/create".equals(path)) { save(req, resp, false); return; }
        if ("/edit".equals(path)) { save(req, resp, true); return; }
        if ("/delete".equals(path)) { delete(req, resp); return; }
        resp.sendError(404);
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("notes", noteDAO.findAll());
        req.setAttribute("reports", reportDAO.findAll(null));
        req.setAttribute("pageTitle", "Executive Notes");
        req.getRequestDispatcher("/WEB-INF/views/notes.jsp").forward(req, resp);
    }

    private void showForm(HttpServletRequest req, HttpServletResponse resp, ExecutiveNote note, boolean edit) throws ServletException, IOException {
        if (!edit) {
            long reportId = WebUtil.parseLong(req.getParameter("reportId"), -1);
            if (reportId > 0) note.setReportId(reportId);
            note.setPriority("NORMAL");
        }
        req.setAttribute("note", note);
        req.setAttribute("reports", reportDAO.findAll(null));
        req.setAttribute("editMode", edit);
        req.setAttribute("pageTitle", edit ? "Edit Executive Note" : "Create Executive Note");
        req.getRequestDispatcher("/WEB-INF/views/note-form.jsp").forward(req, resp);
    }

    private void save(HttpServletRequest req, HttpServletResponse resp, boolean edit) throws ServletException, IOException {
        ExecutiveNote n = new ExecutiveNote();
        if (edit) n.setId(WebUtil.parseLong(req.getParameter("id"), -1));
        long reportId = WebUtil.parseLong(req.getParameter("reportId"), -1);
        n.setReportId(reportId > 0 ? reportId : null);
        n.setTitle(WebUtil.clean(req.getParameter("title"), 160));
        n.setNoteText(WebUtil.clean(req.getParameter("noteText"), 3000));
        n.setPriority(WebUtil.priority(req.getParameter("priority")));
        Object user = req.getSession().getAttribute("displayName");
        n.setAuthor(user == null ? "Director" : String.valueOf(user));

        if (n.getTitle() == null || n.getNoteText() == null) {
            req.setAttribute("formError", "Title and note text are required.");
            showForm(req, resp, n, edit); return;
        }
        if (edit) {
            if (noteDAO.findById(n.getId()) == null) { resp.sendError(404); return; }
            noteDAO.update(n);
            WebUtil.flash(req, "success", "Executive note updated in TXT storage.");
        } else {
            noteDAO.create(n);
            WebUtil.flash(req, "success", "Executive note created in TXT storage.");
        }
        if (n.getReportId() != null) resp.sendRedirect(req.getContextPath() + "/executive/reports/view?id=" + n.getReportId());
        else resp.sendRedirect(req.getContextPath() + "/executive/notes/");
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long id = WebUtil.parseLong(req.getParameter("id"), -1);
        long reportId = WebUtil.parseLong(req.getParameter("reportId"), -1);
        boolean deleted = noteDAO.delete(id);
        WebUtil.flash(req, deleted ? "success" : "warning", deleted ? "Executive note deleted from TXT storage." : "Note was not found.");
        if (reportId > 0) resp.sendRedirect(req.getContextPath() + "/executive/reports/view?id=" + reportId);
        else resp.sendRedirect(req.getContextPath() + "/executive/notes/");
    }
}
