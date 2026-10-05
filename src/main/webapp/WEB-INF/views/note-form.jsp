<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.hotelworkforce.executive.model.ExecutiveNote" %>
<%@ page import="com.hotelworkforce.executive.model.ReportDefinition" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<%
ExecutiveNote n = (ExecutiveNote) request.getAttribute("note");
List<ReportDefinition> reports = (List<ReportDefinition>) request.getAttribute("reports");
boolean editMode = Boolean.TRUE.equals(request.getAttribute("editMode"));
%>
<div class="page-actions reveal-card"><div><span class="section-kicker">Executive Note CRUD</span><h2><%=editMode?"Update Note":"Create Note"%></h2><p>Attach a management observation to a report, or keep it as an independent executive action note.</p></div><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/notes/">← Back</a></div>
<% if(request.getAttribute("formError")!=null){ %><div class="flash danger"><span><%=Html.escape(request.getAttribute("formError"))%></span></div><% } %>
<section class="panel form-panel reveal-card"><form class="executive-form" method="post" action="<%=request.getContextPath()%>/executive/notes/<%=editMode?"edit":"create"%>">
<% if(editMode){ %><input type="hidden" name="id" value="<%=n.getId()%>"><% } %>
<div class="form-grid two">
    <div class="field span-2"><label>Title <b>*</b></label><input required maxlength="160" name="title" value="<%=Html.escape(n.getTitle())%>" placeholder="e.g. Investigate higher absence trend"></div>
    <div class="field"><label>Priority</label><select name="priority"><% String[] priorities={"LOW","NORMAL","HIGH","CRITICAL"}; for(String p:priorities){ %><option value="<%=p%>" <%=p.equals(n.getPriority())?"selected":""%>><%=p%></option><% } %></select></div>
    <div class="field"><label>Linked Report</label><select name="reportId"><option value="">Independent note</option><% for(ReportDefinition r:reports){ %><option value="<%=r.getId()%>" <%=n.getReportId()!=null&&n.getReportId()==r.getId()?"selected":""%>><%=Html.escape(r.getName())%></option><% } %></select></div>
    <div class="field span-2"><label>Management Note <b>*</b></label><textarea required maxlength="3000" rows="8" name="noteText" placeholder="Write the decision, risk, action, or observation..."><%=Html.escape(n.getNoteText())%></textarea></div>
</div>
<div class="form-actions"><a class="btn btn-ghost" href="<%=request.getContextPath()%>/executive/notes/">Cancel</a><button class="btn btn-primary" type="submit"><%=editMode?"Save Changes":"Create Note"%></button></div>
</form></section>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
