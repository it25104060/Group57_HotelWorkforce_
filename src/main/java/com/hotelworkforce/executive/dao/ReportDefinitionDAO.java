package com.hotelworkforce.executive.dao;

import com.hotelworkforce.executive.config.StorageManager;
import com.hotelworkforce.executive.model.ReportDefinition;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReportDefinitionDAO {
    private static final String FILE = "report_definitions.txt";
    private static final String HEADER = "id|name|description|reportType|department|roleFilter|dateFrom|dateTo|status|createdBy|createdAt|updatedAt";

    public long create(ReportDefinition report) {
        synchronized (StorageManager.LOCK) {
            List<ReportDefinition> all = loadAll();
            long id = all.stream().mapToLong(ReportDefinition::getId).max().orElse(0) + 1;
            LocalDateTime now = LocalDateTime.now();
            report.setId(id);
            report.setCreatedAt(now);
            report.setUpdatedAt(now);
            all.add(report);
            saveAll(all);
            return id;
        }
    }

    public ReportDefinition findById(long id) {
        synchronized (StorageManager.LOCK) {
            return loadAll().stream().filter(r -> r.getId() == id).findFirst().orElse(null);
        }
    }

    public List<ReportDefinition> findAll(String search) {
        synchronized (StorageManager.LOCK) {
            String q = search == null ? null : search.trim().toLowerCase(Locale.ROOT);
            List<ReportDefinition> list = new ArrayList<>();
            for (ReportDefinition r : loadAll()) {
                if (q == null || q.isEmpty() || contains(r.getName(), q) || contains(r.getDescription(), q) || contains(r.getReportType(), q)) {
                    list.add(r);
                }
            }
            list.sort(Comparator.comparing(ReportDefinition::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingLong(ReportDefinition::getId).reversed());
            return list;
        }
    }

    public void update(ReportDefinition report) {
        synchronized (StorageManager.LOCK) {
            List<ReportDefinition> all = loadAll();
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                ReportDefinition old = all.get(i);
                if (old.getId() == report.getId()) {
                    report.setCreatedBy(old.getCreatedBy());
                    report.setCreatedAt(old.getCreatedAt());
                    report.setUpdatedAt(LocalDateTime.now());
                    all.set(i, report);
                    found = true;
                    break;
                }
            }
            if (found) saveAll(all);
        }
    }

    public boolean delete(long id) {
        synchronized (StorageManager.LOCK) {
            List<ReportDefinition> all = loadAll();
            boolean removed = all.removeIf(r -> r.getId() == id);
            if (removed) {
                saveAll(all);
                detachNotes(id);
            }
            return removed;
        }
    }

    public int count() {
        synchronized (StorageManager.LOCK) {
            return loadAll().size();
        }
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    private List<ReportDefinition> loadAll() {
        List<ReportDefinition> list = new ArrayList<>();
        for (String line : StorageManager.readDataLines(FILE)) {
            List<String> p = StorageManager.split(line);
            ReportDefinition r = new ReportDefinition();
            r.setId(StorageManager.longValue(StorageManager.value(p, 0), -1));
            r.setName(StorageManager.value(p, 1));
            r.setDescription(StorageManager.value(p, 2));
            r.setReportType(StorageManager.value(p, 3));
            r.setDepartment(StorageManager.value(p, 4));
            r.setRoleFilter(StorageManager.value(p, 5));
            r.setDateFrom(parseDate(StorageManager.value(p, 6)));
            r.setDateTo(parseDate(StorageManager.value(p, 7)));
            r.setStatus(StorageManager.value(p, 8));
            r.setCreatedBy(StorageManager.value(p, 9));
            r.setCreatedAt(parseDateTime(StorageManager.value(p, 10)));
            r.setUpdatedAt(parseDateTime(StorageManager.value(p, 11)));
            if (r.getId() > 0) list.add(r);
        }
        return list;
    }

    private void saveAll(List<ReportDefinition> list) {
        List<String> lines = new ArrayList<>();
        for (ReportDefinition r : list) {
            lines.add(StorageManager.join(
                    String.valueOf(r.getId()), r.getName(), r.getDescription(), r.getReportType(),
                    r.getDepartment(), r.getRoleFilter(), str(r.getDateFrom()), str(r.getDateTo()),
                    r.getStatus(), r.getCreatedBy(), str(r.getCreatedAt()), str(r.getUpdatedAt())
            ));
        }
        StorageManager.writeDataLines(FILE, HEADER, lines);
    }

    private void detachNotes(long reportId) {
        final String notesFile = "executive_notes.txt";
        List<String> output = new ArrayList<>();
        for (String line : StorageManager.readDataLines(notesFile)) {
            List<String> p = StorageManager.split(line);
            long linked = StorageManager.longValue(StorageManager.value(p, 1), -1);
            if (linked == reportId) {
                output.add(StorageManager.join(
                        StorageManager.value(p, 0), null, StorageManager.value(p, 2), StorageManager.value(p, 3),
                        StorageManager.value(p, 4), StorageManager.value(p, 5), StorageManager.value(p, 6), StorageManager.value(p, 7)
                ));
            } else {
                output.add(line);
            }
        }
        StorageManager.writeDataLines(notesFile, "id|reportId|title|noteText|priority|author|createdAt|updatedAt", output);
    }

    private LocalDate parseDate(String v) {
        try { return v == null ? null : LocalDate.parse(v); } catch (Exception ex) { return null; }
    }

    private LocalDateTime parseDateTime(String v) {
        try { return v == null ? null : LocalDateTime.parse(v); } catch (Exception ex) { return null; }
    }

    private String str(Object value) { return value == null ? null : String.valueOf(value); }
}
