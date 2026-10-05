package com.hotelworkforce.executive.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Plain-text storage used by the Reporting & Executive Analytics module.
 * No database server, JDBC driver or SQL schema is required.
 */
public final class StorageManager {
    public static final Object LOCK = new Object();
    private static volatile Path storageDirectory;

    private StorageManager() {}

    public static void configure(Path directory) {
        if (directory == null) throw new IllegalArgumentException("Storage directory cannot be null");
        storageDirectory = directory.toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageDirectory);
        } catch (IOException ex) {
            throw new StorageException("Unable to create storage directory: " + storageDirectory, ex);
        }
    }

    public static Path getStorageDirectory() {
        Path configured = storageDirectory;
        if (configured != null) return configured;

        String external = System.getenv("HOTEL_WORKFORCE_STORAGE_DIR");
        if (external != null && !external.trim().isEmpty()) {
            configure(Paths.get(external.trim()));
            return storageDirectory;
        }

        // Developer fallback. Under Tomcat, FileStorageBootstrapListener configures WEB-INF/storage.
        configure(Paths.get(System.getProperty("user.dir"), "storage"));
        return storageDirectory;
    }

    public static Path file(String fileName) {
        return getStorageDirectory().resolve(fileName).normalize();
    }

    public static List<String> readDataLines(String fileName) {
        Path path = file(fileName);
        try {
            if (!Files.exists(path)) return new ArrayList<>();
            List<String> all = Files.readAllLines(path, StandardCharsets.UTF_8);
            List<String> data = new ArrayList<>();
            for (String line : all) {
                if (line == null) continue;
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) continue;
                data.add(line);
            }
            return data;
        } catch (IOException ex) {
            throw new StorageException("Unable to read " + path, ex);
        }
    }

    public static void writeDataLines(String fileName, String header, List<String> dataLines) {
        Path path = file(fileName);
        try {
            Files.createDirectories(path.getParent());
            List<String> output = new ArrayList<>();
            if (header != null && !header.trim().isEmpty()) {
                for (String line : header.split("\\R")) output.add("# " + line);
            }
            output.addAll(dataLines == null ? Collections.emptyList() : dataLines);

            Path temp = path.resolveSibling(path.getFileName().toString() + ".tmp");
            Files.write(temp, output, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            throw new StorageException("Unable to write " + path, ex);
        }
    }

    public static String join(String... values) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) out.append('|');
            out.append(escape(values[i]));
        }
        return out.toString();
    }

    public static List<String> split(String line) {
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (escaped) {
                switch (ch) {
                    case 'n': current.append('\n'); break;
                    case 'r': current.append('\r'); break;
                    case 't': current.append('\t'); break;
                    case '|': current.append('|'); break;
                    case '\\': current.append('\\'); break;
                    default: current.append(ch); break;
                }
                escaped = false;
            } else if (ch == '\\') {
                escaped = true;
            } else if (ch == '|') {
                parts.add(emptyToNull(current.toString()));
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        if (escaped) current.append('\\');
        parts.add(emptyToNull(current.toString()));
        return parts;
    }

    public static String value(List<String> parts, int index) {
        return index >= 0 && index < parts.size() ? parts.get(index) : null;
    }

    public static long longValue(String value, long defaultValue) {
        try { return value == null ? defaultValue : Long.parseLong(value.trim()); }
        catch (NumberFormatException ex) { return defaultValue; }
    }

    public static int intValue(String value, int defaultValue) {
        try { return value == null ? defaultValue : Integer.parseInt(value.trim()); }
        catch (NumberFormatException ex) { return defaultValue; }
    }

    public static double doubleValue(String value, double defaultValue) {
        try { return value == null ? defaultValue : Double.parseDouble(value.trim()); }
        catch (NumberFormatException ex) { return defaultValue; }
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }

    private static String emptyToNull(String value) {
        return value == null || value.isEmpty() ? null : value;
    }
}
