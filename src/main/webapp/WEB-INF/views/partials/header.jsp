<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.hotelworkforce.executive.util.Html" %>
<%
String ctx = request.getContextPath();
String uri = request.getRequestURI();
String pageTitle = (String) request.getAttribute("pageTitle");
if (pageTitle == null) pageTitle = "Reporting & Executive Analytics";
String displayName = String.valueOf(session.getAttribute("displayName") == null ? "Hotel Director" : session.getAttribute("displayName"));
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="theme-color" content="#16A34A">
    <title><%= Html.escape(pageTitle) %> | Hotel Workforce</title>
    <link rel="stylesheet" href="<%=ctx%>/assets/css/executive.css">
</head>
<body>
<div class="ambient ambient-one"></div>
<div class="ambient ambient-two"></div>
<div class="app-layout">
    <aside class="sidebar" id="sidebar">
        <a class="brand" href="<%=ctx%>/executive/dashboard">
            <span class="brand-mark">HW</span>
            <span><strong>Hotel Workforce</strong><small>Executive Analytics</small></span>
        </a>
        <nav class="side-nav">
            <a class="<%= uri.contains("/executive/dashboard") ? "active" : "" %>" href="<%=ctx%>/executive/dashboard"><span>⌂</span> Dashboard</a>
            <a class="<%= uri.contains("/executive/reports") ? "active" : "" %>" href="<%=ctx%>/executive/reports/"><span>▤</span> Executive Reports</a>
            <a class="<%= uri.contains("/executive/notes") ? "active" : "" %>" href="<%=ctx%>/executive/notes/"><span>✎</span> Executive Notes</a>
            <a href="<%=ctx%>/executive/reports/create"><span>＋</span> Create Report</a>
        </nav>
        <div class="side-profile">
            <div class="avatar"><%= Html.escape(displayName.substring(0,1).toUpperCase()) %></div>
            <div><strong><%= Html.escape(displayName) %></strong><small>Hotel Director</small></div>
        </div>
    </aside>
    <main class="main-panel">
        <header class="topbar">
            <button class="icon-btn mobile-menu" type="button" aria-label="Open menu" data-sidebar-toggle>☰</button>
            <div>
                <p class="eyebrow">Golden Palm Hotel · Sri Lanka</p>
                <h1><%= Html.escape(pageTitle) %></h1>
            </div>
            <div class="topbar-actions">
                <span class="live-pill"><i></i> Live Analytics</span>
            </div>
        </header>
        <section class="content-wrap">
        <% if (request.getAttribute("flashMessage") != null) { %>
            <div class="flash <%= Html.escape(request.getAttribute("flashType")) %>" data-auto-dismiss>
                <span><%= Html.escape(request.getAttribute("flashMessage")) %></span>
                <button type="button" onclick="this.parentElement.remove()">×</button>
            </div>
        <% } %>
