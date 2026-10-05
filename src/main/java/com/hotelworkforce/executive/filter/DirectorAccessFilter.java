package com.hotelworkforce.executive.filter;

import com.hotelworkforce.executive.config.AppConfig;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/executive/*")
public class DirectorAccessFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        String role = (String) session.getAttribute("userRole");

        if (role == null && "DEV".equalsIgnoreCase(AppConfig.appMode())) {
            session.setAttribute("userRole", "DIRECTOR");
            session.setAttribute("displayName", "Ramesh Gunasekara");
            role = "DIRECTOR";
        }

        if (!"DIRECTOR".equalsIgnoreCase(role) && !"GENERAL_MANAGER".equalsIgnoreCase(role)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Director access is required for Reporting & Executive Analytics.");
            return;
        }
        chain.doFilter(request, response);
    }
}
