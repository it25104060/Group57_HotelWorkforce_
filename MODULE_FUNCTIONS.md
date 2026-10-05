# Module 7 — Reporting & Executive Analytics

This project contains only the **Reporting & Executive Analytics / Executive Management** module for Hotel Workforce.

## Functional scope

- Executive dashboard for:
  - Headcount
  - Attendance rate
  - Absences and late arrivals
  - Approved leave requests and leave days
  - Payroll expenditure
  - Overtime cost
  - Deductions
- Filters by department, employee role, start date and end date.
- Graphs for headcount by department, attendance trend, leave by type and payroll trend.
- PDF export.
- CSV export.

## Complete CRUD included

### Saved Executive Reports
- Create a saved report configuration.
- Read/list/search/view saved reports.
- Update report name, description, type, filters, dates and status.
- Delete obsolete reports.

### Executive Notes
- Create management notes.
- Read/list notes and notes linked to a report.
- Update note content, priority and linked report.
- Delete notes.

## Storage

There is **no database connection** and no JDBC dependency.

All data is stored in plain `.txt` files under:

`src/main/webapp/WEB-INF/storage/`

Files:
- `employees.txt`
- `attendance_records.txt`
- `leave_requests.txt`
- `payroll_records.txt`
- `report_definitions.txt`
- `executive_notes.txt`

The application reads the source workforce data from these files and writes report/note CRUD changes back to the relevant TXT files.
