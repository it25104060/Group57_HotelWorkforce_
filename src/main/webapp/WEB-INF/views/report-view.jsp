<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.ReportDefinition" %>
<%@ page import="com.hotelworkforce.executive.model.ExecutiveNote" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
ReportDefinition report = (ReportDefinition) request.getAttribute("report");
List<ExecutiveNote> notes = (List<ExecutiveNote>) request.getAttribute("notes");
%>
<section class="report-banner reveal-card">
    <div><span class="status <%=Html.escape(report.getStatus().toLowerCase())%>"><%=Html.escape(report.getStatus())%></span><span class="type-tag"><%=Html.escape(report.getReportType())%></span><h2><%=Html.escape(report.getName())%></h2><p><%=Html.escape(report.getDescription())%></p>
        <div class="report-meta"><span>Department: <b><%=Html.escape(report.getDepartment()==null?"All":report.getDepartment())%></b></span><span>Role: <b><%=Html.escape(report.getRoleFilter()==null?"All":report.getRoleFilter())%></b></span><span>Range: <b><%=report.getDateFrom()==null?"Any":report.getDateFrom()%> → <%=report.getDateTo()==null?"Any":report.getDateTo()%></b></span></div>
    </div>
    <div class="banner-actions"><a class="btn btn-soft" href="<%=request.getContextPath()%>/executive/reports/edit?id=<%=report.getId()%>">Edit Report</a><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/export?id=<%=report.getId()%>&format=pdf">⇩ PDF</a><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/export?id=<%=report.getId()%>&format=csv">⇩ CSV</a></div>
</section>

<%@ include file="partials/analytics.jspf" %>

<section class="panel reveal-card">
    <div class="panel-head"><div><span class="section-kicker">Executive Management</span><h3>Management Notes</h3><p>Record decisions, risks and follow-up items connected to this report.</p></div><a class="btn btn-soft" href="<%=request.getContextPath()%>/executive/notes/create?reportId=<%=report.getId()%>">＋ Add Note</a></div>
    <% if(notes.isEmpty()){ %><div class="empty-inline">No notes attached to this report yet.</div><% } else { %>
    <div class="notes-grid"><% for(ExecutiveNote n:notes){ %>
        <article class="note-card priority-<%=Html.escape(n.getPriority().toLowerCase())%>"><div class="note-top"><span><%=Html.escape(n.getPriority())%></span><small><%=n.getUpdatedAt()==null?"":n.getUpdatedAt().toLocalDate()%></small></div><h4><%=Html.escape(n.getTitle())%></h4><p><%=Html.escape(n.getNoteText())%></p><div class="row-actions"><a href="<%=request.getContextPath()%>/executive/notes/edit?id=<%=n.getId()%>">Edit</a><form method="post" action="<%=request.getContextPath()%>/executive/notes/delete" onsubmit="return confirm('Delete this executive note?');"><input type="hidden" name="id" value="<%=n.getId()%>"><input type="hidden" name="reportId" value="<%=report.getId()%>"><button class="danger-link" type="submit">Delete</button></form></div></article>
    <% } %></div><% } %>
</section>
<div class="bottom-actions"><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/reports/">← All Reports</a><form method="post" action="<%=request.getContextPath()%>/executive/reports/delete" onsubmit="return confirm('Permanently delete this report? Linked notes will remain but become unlinked.');"><input type="hidden" name="id" value="<%=report.getId()%>"><button class="btn btn-danger" type="submit">Delete Report</button></form></div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
