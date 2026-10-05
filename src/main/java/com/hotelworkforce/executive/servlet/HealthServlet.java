package com.hotelworkforce.executive.servlet;

import com.hotelworkforce.executive.config.StorageManager;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/executive/health")
public class HealthServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json; charset=UTF-8");
        try {
            Path dir = StorageManager.getStorageDirectory();
            boolean ok = Files.isDirectory(dir) && Files.isReadable(dir) && Files.isWritable(dir);
            if (ok) {
                resp.getWriter().write("{\"status\":\"UP\",\"storage\":\"TXT_FILES\",\"module\":\"Reporting & Executive Analytics\"}");
            } else {
                resp.setStatus(500);
                resp.getWriter().write("{\"status\":\"DOWN\",\"storage\":\"NOT_WRITABLE\"}");
            }
        } catch (Exception ex) {
            resp.setStatus(500);
            String msg = ex.getMessage() == null ? "TXT storage error" : ex.getMessage().replace("\\", "\\\\").replace("\"", "\\\"");
            resp.getWriter().write("{\"status\":\"DOWN\",\"storage\":\"ERROR\",\"message\":\"" + msg + "\"}");
        }
    }
}
