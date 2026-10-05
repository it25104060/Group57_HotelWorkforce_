package com.hotelworkforce.executive.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebListener
public class FileStorageBootstrapListener implements ServletContextListener {
    private static final String[] FILES = {
            "employees.txt",
            "attendance_records.txt",
            "leave_requests.txt",
            "payroll_records.txt",
            "report_definitions.txt",
            "executive_notes.txt"
    };

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        try {
            String external = System.getenv("HOTEL_WORKFORCE_STORAGE_DIR");
            Path storage;
            if (external != null && !external.trim().isEmpty()) {
                storage = Paths.get(external.trim());
            } else {
                String real = ctx.getRealPath("/WEB-INF/storage");
                if (real == null) {
                    throw new IllegalStateException("Tomcat must unpack the WAR so WEB-INF/storage can be used as writable TXT storage. Keep unpackWARs=true (Tomcat default), or set HOTEL_WORKFORCE_STORAGE_DIR.");
                }
                storage = Paths.get(real);
            }
            StorageManager.configure(storage);
            Files.createDirectories(storage);
            for (String file : FILES) {
                Path p = storage.resolve(file);
                if (!Files.exists(p)) Files.createFile(p);
            }
            ctx.log("Reporting & Executive Analytics TXT storage: " + storage.toAbsolutePath());
        } catch (Exception ex) {
            ctx.log("TXT storage initialization failed.", ex);
            throw new IllegalStateException("Unable to initialize TXT storage", ex);
        }
    }
}
