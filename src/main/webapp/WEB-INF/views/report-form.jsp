<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.ReportDefinition" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
ReportDefinition r = (ReportDefinition) request.getAttribute("report");
boolean editMode = Boolean.TRUE.equals(request.getAttribute("editMode"));
List<String> departments = (List<String>) request.getAttribute("departments");
List<String> roles = (List<String>) request.getAttribute("roles");
%>
<div class="page-actions reveal-card"><div><span class="section-kicker">Report CRUD</span><h2><%=editMode?"Update Saved Report":"Create Saved Report"%></h2><p>Define the executive reporting scope. The dashboard will aggregate headcount, attendance, leave and payroll for this saved view.</p></div><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/reports/">← Back</a></div>
<% if (request.getAttribute("formError") != null) { %><div class="flash danger"><span><%=Html.escape(request.getAttribute("formError"))%></span></div><% } %>
<section class="panel form-panel reveal-card">
<form class="executive-form" method="post" action="<%=request.getContextPath()%>/executive/reports/<%=editMode?"edit":"create"%>">
    <% if(editMode){ %><input type="hidden" name="id" value="<%=r.getId()%>"><% } %>
    <div class="form-grid two">
        <div class="field span-2"><label>Report Name <b>*</b></label><input required minlength="3" maxlength="140" type="text" name="name" placeholder="e.g. Monthly Workforce Overview" value="<%=Html.escape(r.getName())%>"></div>
        <div class="field span-2"><label>Description</label><textarea name="description" maxlength="1200" rows="4" placeholder="Purpose of this report and what management should review..."><%=Html.escape(r.getDescription())%></textarea></div>
        <div class="field"><label>Report Focus</label><select name="reportType">
            <% String[] types={"WORKFORCE","HEADCOUNT","ATTENDANCE","LEAVE","PAYROLL"}; for(String t:types){ %><option value="<%=t%>" <%=t.equals(r.getReportType())?"selected":""%>><%=t%></option><% } %>
        </select></div>
        <div class="field"><label>Status</label><select name="status"><% String[] sts={"ACTIVE","DRAFT","ARCHIVED"}; for(String s:sts){ %><option value="<%=s%>" <%=s.equals(r.getStatus())?"selected":""%>><%=s%></option><% } %></select></div>
        <div class="field"><label>Department</label><select name="department"><option value="ALL">All departments</option><% for(String d:departments){ %><option value="<%=Html.escape(d)%>" <%=d.equals(r.getDepartment())?"selected":""%>><%=Html.escape(d)%></option><% } %></select></div>
        <div class="field"><label>Role</label><select name="role"><option value="ALL">All roles</option><% for(String role:roles){ %><option value="<%=Html.escape(role)%>" <%=role.equals(r.getRoleFilter())?"selected":""%>><%=Html.escape(role)%></option><% } %></select></div>
        <div class="field"><label>From Date</label><input type="date" name="dateFrom" value="<%=r.getDateFrom()==null?"":r.getDateFrom()%>"></div>
        <div class="field"><label>To Date</label><input type="date" name="dateTo" value="<%=r.getDateTo()==null?"":r.getDateTo()%>"></div>
    </div>
    <div class="form-help"><span>✓</span><p>Saved reports are reusable definitions. The latest underlying workforce data is queried each time you open or export the report.</p></div>
    <div class="form-actions"><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/reports/">Cancel</a><button class="btn btn-primary" type="submit"><%=editMode?"Save Changes":"Create Report"%></button></div>
</form>
</section>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
