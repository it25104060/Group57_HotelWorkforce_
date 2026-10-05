package com.hotelworkforce.executive.dao;

import com.hotelworkforce.executive.config.StorageManager;
import com.hotelworkforce.executive.model.ExecutiveNote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExecutiveNoteDAO {
    private static final String FILE = "executive_notes.txt";
    private static final String HEADER = "id|reportId|title|noteText|priority|author|createdAt|updatedAt";

    public long create(ExecutiveNote note) {
        synchronized (StorageManager.LOCK) {
            List<ExecutiveNote> all = loadAll();
            long id = all.stream().mapToLong(ExecutiveNote::getId).max().orElse(0) + 1;
            LocalDateTime now = LocalDateTime.now();
            note.setId(id);
            note.setCreatedAt(now);
            note.setUpdatedAt(now);
            all.add(note);
            saveAll(all);
            return id;
        }
    }

    public ExecutiveNote findById(long id) {
        synchronized (StorageManager.LOCK) {
            return loadAll().stream().filter(n -> n.getId() == id).findFirst().orElse(null);
        }
    }

    public List<ExecutiveNote> findAll() {
        synchronized (StorageManager.LOCK) {
            List<ExecutiveNote> list = loadAll();
            list.sort(Comparator.comparing(ExecutiveNote::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparingLong(ExecutiveNote::getId).reversed());
            return list;
        }
    }

    public List<ExecutiveNote> findByReportId(long reportId) {
        synchronized (StorageManager.LOCK) {
            List<ExecutiveNote> list = new ArrayList<>();
            for (ExecutiveNote n : loadAll()) {
                if (n.getReportId() != null && n.getReportId() == reportId) list.add(n);
            }
            list.sort(Comparator.comparing(ExecutiveNote::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            return list;
        }
    }

    public void update(ExecutiveNote note) {
        synchronized (StorageManager.LOCK) {
            List<ExecutiveNote> all = loadAll();
            boolean found = false;
            for (int i = 0; i < all.size(); i++) {
                ExecutiveNote old = all.get(i);
                if (old.getId() == note.getId()) {
                    note.setAuthor(old.getAuthor());
                    note.setCreatedAt(old.getCreatedAt());
                    note.setUpdatedAt(LocalDateTime.now());
                    all.set(i, note);
                    found = true;
                    break;
                }
            }
            if (found) saveAll(all);
        }
    }

    public boolean delete(long id) {
        synchronized (StorageManager.LOCK) {
            List<ExecutiveNote> all = loadAll();
            boolean removed = all.removeIf(n -> n.getId() == id);
            if (removed) saveAll(all);
            return removed;
        }
    }

    public int count() {
        synchronized (StorageManager.LOCK) { return loadAll().size(); }
    }

    private List<ExecutiveNote> loadAll() {
        List<ExecutiveNote> list = new ArrayList<>();
        for (String line : StorageManager.readDataLines(FILE)) {
            List<String> p = StorageManager.split(line);
            ExecutiveNote n = new ExecutiveNote();
            n.setId(StorageManager.longValue(StorageManager.value(p, 0), -1));
            long reportId = StorageManager.longValue(StorageManager.value(p, 1), -1);
            n.setReportId(reportId > 0 ? reportId : null);
            n.setTitle(StorageManager.value(p, 2));
            n.setNoteText(StorageManager.value(p, 3));
            n.setPriority(StorageManager.value(p, 4));
            n.setAuthor(StorageManager.value(p, 5));
            n.setCreatedAt(parseDateTime(StorageManager.value(p, 6)));
            n.setUpdatedAt(parseDateTime(StorageManager.value(p, 7)));
            if (n.getId() > 0) list.add(n);
        }
        return list;
    }

    private void saveAll(List<ExecutiveNote> list) {
        List<String> lines = new ArrayList<>();
        for (ExecutiveNote n : list) {
            lines.add(StorageManager.join(
                    String.valueOf(n.getId()), n.getReportId() == null ? null : String.valueOf(n.getReportId()),
                    n.getTitle(), n.getNoteText(), n.getPriority(), n.getAuthor(),
                    str(n.getCreatedAt()), str(n.getUpdatedAt())
            ));
        }
        StorageManager.writeDataLines(FILE, HEADER, lines);
    }

    private LocalDateTime parseDateTime(String v) {
        try { return v == null ? null : LocalDateTime.parse(v); } catch (Exception ex) { return null; }
    }

    private String str(Object value) { return value == null ? null : String.valueOf(value); }
}
