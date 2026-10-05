<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.ReportDefinition" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
List<ReportDefinition> reports = (List<ReportDefinition>) request.getAttribute("reports");
String search = (String) request.getAttribute("search");
%>
<div class="page-actions reveal-card">
    <div><span class="section-kicker">CRUD Management</span><h2>Saved Executive Reports</h2><p>Create reusable analytical views, open them anytime, update filters, or delete obsolete reports.</p></div>
    <a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/reports/create">＋ New Report</a>
</div>
<section class="panel reveal-card">
    <form class="search-row" method="get" action="<%=request.getContextPath()%>/executive/reports/">
        <input type="search" name="q" placeholder="Search by report name, description or type..." value="<%=Html.escape(search)%>">
        <button class="btn btn-soft" type="submit">Search</button>
        <% if (search != null) { %><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/reports/">Clear</a><% } %>
    </form>
    <% if (reports.isEmpty()) { %>
        <div class="empty-state"><div>▤</div><h3>No reports found</h3><p>Create your first executive report or change the search.</p><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/reports/create">Create report</a></div>
    <% } else { %>
    <div class="table-wrap">
        <table class="data-table">
            <thead><tr><th>Report</th><th>Type</th><th>Scope</th><th>Date Range</th><th>Status</th><th>Updated</th><th class="right">Actions</th></tr></thead>
            <tbody>
            <% for (ReportDefinition r : reports) { %>
                <tr>
                    <td><strong><%=Html.escape(r.getName())%></strong><small><%=Html.escape(r.getDescription())%></small></td>
                    <td><span class="type-tag"><%=Html.escape(r.getReportType())%></span></td>
                    <td><%=Html.escape(r.getDepartment()==null?"All departments":r.getDepartment())%><small><%=Html.escape(r.getRoleFilter()==null?"All roles":r.getRoleFilter())%></small></td>
                    <td><%= r.getDateFrom()==null?"Any":r.getDateFrom() %><small>to <%= r.getDateTo()==null?"Any":r.getDateTo() %></small></td>
                    <td><span class="status <%=Html.escape(r.getStatus().toLowerCase())%>"><%=Html.escape(r.getStatus())%></span></td>
                    <td><%= r.getUpdatedAt()==null?"—":r.getUpdatedAt().toLocalDate() %></td>
                    <td class="right"><div class="row-actions">
                        <a title="Open" href="<%=request.getContextPath()%>/executive/reports/view?id=<%=r.getId()%>">View</a>
                        <a title="Edit" href="<%=request.getContextPath()%>/executive/reports/edit?id=<%=r.getId()%>">Edit</a>
                        <form method="post" action="<%=request.getContextPath()%>/executive/reports/delete" onsubmit="return confirm('Delete this saved executive report?');"><input type="hidden" name="id" value="<%=r.getId()%>"><button class="danger-link" type="submit">Delete</button></form>
                    </div></td>
                </tr>
            <% } %>
            </tbody>
        </table>
    </div>
    <% } %>
</section>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
