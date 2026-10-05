<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<% if(request.getAttribute("pageTitle")==null) request.setAttribute("pageTitle","System Error"); String error=(String)request.getAttribute("errorMessage"); if(error==null) error="The request could not be completed. Check the application log and TXT storage files."; %>
<jsp:include page="/WEB-INF/views/partials/header.jsp" />
<div class="error-page reveal-card"><div class="error-code">!</div><h2>Unable to complete the request.</h2><p><%=Html.escape(error)%></p><div class="inline-actions"><a class="btn btn-primary" href="<%=request.getContextPath()%>/executive/dashboard">Dashboard</a></div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
