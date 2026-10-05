<%@ page contentType="text/html; charset=UTF-8" %>
<% request.setAttribute("pageTitle","Not Found"); %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<div class="error-page reveal-card"><div class="error-code">404</div><h2>That executive resource was not found.</h2><p>The report, note, or route may have been deleted or changed.</p><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/dashboard">Back to Dashboard</a></div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
