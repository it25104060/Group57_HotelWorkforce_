<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.ExecutiveNote" %>
<%@ page import="com.hotelworkforce.executive.model.ReportDefinition" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
List<ExecutiveNote> notes = (List<ExecutiveNote>) request.getAttribute("notes");
List<ReportDefinition> reports = (List<ReportDefinition>) request.getAttribute("reports");
java.util.Map<Long,String> reportNames = new java.util.HashMap<>();
for(ReportDefinition r:reports) reportNames.put(r.getId(),r.getName());
%>
<div class="page-actions reveal-card"><div><span class="section-kicker">CRUD Management</span><h2>Executive Notes</h2><p>Maintain strategic observations, risks and follow-up actions. Notes can be linked to a saved report or kept independent.</p></div><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/notes/create">＋ New Note</a></div>
<section class="panel reveal-card">
<% if(notes.isEmpty()){ %><div class="empty-state"><div>✎</div><h3>No executive notes yet</h3><p>Create a note to capture management observations.</p><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/notes/create">Create note</a></div><% } else { %>
<div class="notes-grid notes-large">
<% for(ExecutiveNote n:notes){ String linked=n.getReportId()==null?null:reportNames.get(n.getReportId()); %>
<article class="note-card priority-<%=Html.escape(n.getPriority().toLowerCase())%>">
    <div class="note-top"><span><%=Html.escape(n.getPriority())%></span><small><%=n.getUpdatedAt()==null?"":n.getUpdatedAt().toLocalDate()%></small></div>
    <h4><%=Html.escape(n.getTitle())%></h4><p><%=Html.escape(n.getNoteText())%></p>
    <div class="note-link"><%= linked==null?"Independent note":"Linked: "+Html.escape(linked) %></div>
    <div class="row-actions"><% if(n.getReportId()!=null){ %><a href="<%=request.getContextPath()%>/executive/reports/view?id=<%=n.getReportId()%>">Open Report</a><% } %><a href="<%=request.getContextPath()%>/executive/notes/edit?id=<%=n.getId()%>">Edit</a><form method="post" action="<%=request.getContextPath()%>/executive/notes/delete" onsubmit="return confirm('Delete this note?');"><input type="hidden" name="id" value="<%=n.getId()%>"><button class="danger-link" type="submit">Delete</button></form></div>
</article>
<% } %>
</div><% } %>
</section>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
