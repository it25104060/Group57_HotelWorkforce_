package org.example.seproject1.model;

import java.sql.Timestamp;

public class AuditLog {
    private int logId;
    private int userId;
    private String actionDescription;
    private Timestamp logTimestamp;

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public Timestamp getLogTimestamp() { return logTimestamp; }
    public void setLogTimestamp(Timestamp logTimestamp) { this.logTimestamp = logTimestamp; }
}