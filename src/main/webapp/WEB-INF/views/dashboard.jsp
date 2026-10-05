<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.AnalyticsFilter" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
AnalyticsFilter f = (AnalyticsFilter) request.getAttribute("filter");
List<String> departments = (List<String>) request.getAttribute("departments");
List<String> roles = (List<String>) request.getAttribute("roles");
Integer reportCount = (Integer) request.getAttribute("reportCount");
Integer noteCount = (Integer) request.getAttribute("noteCount");
%>
<section class="hero-card executive-hero reveal-card">
    <div>
        <span class="hero-chip">Director Command Center</span>
        <h2>Real-time workforce intelligence for faster management decisions.</h2>
        <p>Monitor staffing levels, attendance behavior, leave patterns and payroll expenditure in one executive view.</p>
        <div class="hero-actions">
            <form method="get" action="<%=request.getContextPath()%>/executive/reports/create"><input type="hidden" name="department" value="<%=Html.escape(f.getDepartment())%>"><input type="hidden" name="role" value="<%=Html.escape(f.getRole())%>"><input type="hidden" name="dateFrom" value="<%=f.getDateFrom()==null?"":f.getDateFrom()%>"><input type="hidden" name="dateTo" value="<%=f.getDateTo()==null?"":f.getDateTo()%>"><button class="btn btn-primary" type="submit">＋ Save This View</button></form>
            <a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/reports/">Browse <%= reportCount %> Reports</a>
        </div>
    </div>
    <div class="hero-orbit" aria-hidden="true"><span></span><span></span><span></span><b>ANALYTICS</b></div>
</section>

<section class="panel filter-panel reveal-card">
    <form class="filter-form" method="get" action="<%=request.getContextPath()%>/executive/dashboard">
        <div class="field"><label>Department</label><select name="department"><option value="ALL">All departments</option><% for(String d:departments){ %><option value="<%=Html.escape(d)%>" <%= d.equals(f.getDepartment())?"selected":"" %>><%=Html.escape(d)%></option><% } %></select></div>
        <div class="field"><label>Role</label><select name="role"><option value="ALL">All roles</option><% for(String r:roles){ %><option value="<%=Html.escape(r)%>" <%= r.equals(f.getRole())?"selected":"" %>><%=Html.escape(r)%></option><% } %></select></div>
        <div class="field"><label>From</label><input type="date" name="dateFrom" value="<%= f.getDateFrom()==null?"":f.getDateFrom() %>"></div>
        <div class="field"><label>To</label><input type="date" name="dateTo" value="<%= f.getDateTo()==null?"":f.getDateTo() %>"></div>
        <div class="filter-actions"><button class="btn btn-primary" type="submit">Apply Filters</button><a class="btn btn-soft" href="<%=request.getContextPath()%>/executive/dashboard">Reset</a></div>
    </form>
</section>

<%@ include file="partials/analytics.jspf" %>

<section class="quick-grid">
    <article class="quick-card"><span>▤</span><div><strong><%=reportCount%> saved reports</strong><p>Create, read, update, delete and export reusable executive report views.</p><a href="<%=request.getContextPath()%>/executive/reports/">Manage reports →</a></div></article>
    <article class="quick-card"><span>✎</span><div><strong><%=noteCount%> executive notes</strong><p>Capture management observations and attach them to saved reports.</p><a href="<%=request.getContextPath()%>/executive/notes/">Manage notes →</a></div></article>
    <article class="quick-card"><span>⇩</span><div><strong>PDF & CSV export</strong><p>Download the currently filtered dashboard for board meetings or offline analysis.</p>
        <div class="inline-actions">
            <form method="get" action="<%=request.getContextPath()%>/executive/export"><input type="hidden" name="format" value="pdf"><input type="hidden" name="department" value="<%=Html.escape(f.getDepartment())%>"><input type="hidden" name="role" value="<%=Html.escape(f.getRole())%>"><input type="hidden" name="dateFrom" value="<%=f.getDateFrom()%>"><input type="hidden" name="dateTo" value="<%=f.getDateTo()%>"><button class="text-btn" type="submit">PDF</button></form>
            <form method="get" action="<%=request.getContextPath()%>/executive/export"><input type="hidden" name="format" value="csv"><input type="hidden" name="department" value="<%=Html.escape(f.getDepartment())%>"><input type="hidden" name="role" value="<%=Html.escape(f.getRole())%>"><input type="hidden" name="dateFrom" value="<%=f.getDateFrom()%>"><input type="hidden" name="dateTo" value="<%=f.getDateTo()%>"><button class="text-btn" type="submit">CSV</button></form>
        </div>
    </div></article>
</section>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
