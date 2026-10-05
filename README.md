# Hotel Workforce — Reporting & Executive Analytics

Standalone **Module 7: Reporting & Executive Analytics** for the Hotel Workforce system.

This version is designed for **Tomcat 10** using Java, JSP and Jakarta Servlets. It uses **TXT files only** for storage. It does **not** connect to SQL Server, H2, MySQL, or any other database.

## 1. Module features

The executive dashboard provides:

- Current workforce headcount.
- Attendance percentage.
- Absence count.
- Late attendance count.
- Approved leave request count and approved leave days.
- Total approved payroll expenditure.
- Overtime expenditure.
- Payroll deductions.
- Headcount by department chart.
- Attendance trend chart.
- Leave pattern chart.
- Payroll trend chart.
- Department filter.
- Role filter.
- Date range filter.
- PDF export.
- CSV export.

The module also contains full CRUD functions for:

- Saved Executive Reports — Create, Read, Update and Delete.
- Executive Notes — Create, Read, Update and Delete.

## 2. TXT storage — no database

All application storage files are inside the project at:

```text
src/main/webapp/WEB-INF/storage/
```

The files are:

```text
employees.txt
attendance_records.txt
leave_requests.txt
payroll_records.txt
report_definitions.txt
executive_notes.txt
```

The first four files act as the workforce source data used by the analytics dashboard. The last two files are writable CRUD storage for saved executive reports and executive notes.

TXT records use a pipe-separated format. Lines beginning with `#` are headers/comments. The application automatically escapes `|`, backslashes and line breaks inside text fields.

## 3. Tomcat 10 requirement

Use:

- Java 11 or newer.
- Maven 3.8+.
- Tomcat 10.x.
- `unpackWARs="true"` in Tomcat, which is the normal default.

Tomcat must unpack the WAR because the application writes CRUD changes into `WEB-INF/storage`.

If you intentionally run Tomcat without unpacking WAR files, set an external writable storage directory using:

```text
HOTEL_WORKFORCE_STORAGE_DIR
```

For the normal assignment/demo setup, leave Tomcat `unpackWARs=true` and no environment variable is needed.

## 4. Build

From the project folder run:

```bash
mvn clean package
```

The WAR will be created as:

```text
target/hotel-workforce-reporting.war
```

Copy that WAR to:

```text
<TOMCAT_HOME>/webapps/
```

Start Tomcat and open:

```text
http://localhost:8080/hotel-workforce-reporting/
```

The welcome page redirects to the executive dashboard.

## 5. Important URLs

```text
/executive/dashboard
/executive/reports/
/executive/reports/create
/executive/notes/
/executive/notes/create
/executive/export?format=csv
/executive/export?format=pdf
/executive/health
```

`/executive/health` checks whether the TXT storage folder is available and writable.

## 6. TXT file layouts

### employees.txt

```text
id|name|department|role|active
```

### attendance_records.txt

```text
id|employeeId|attendanceDate|status|lateMinutes
```

Accepted attendance statuses used by analytics: `PRESENT`, `LATE`, `ABSENT`.

### leave_requests.txt

```text
id|employeeId|leaveType|startDate|endDate|daysCount|status
```

Only `APPROVED` leave is included in executive metrics.

### payroll_records.txt

```text
id|employeeId|payPeriod|netSalary|overtimeAmount|deductionAmount|status
```

Only `APPROVED` payroll rows are included in executive metrics.

### report_definitions.txt

```text
id|name|description|reportType|department|roleFilter|dateFrom|dateTo|status|createdBy|createdAt|updatedAt
```

This file is automatically updated by Report CRUD functions.

### executive_notes.txt

```text
id|reportId|title|noteText|priority|author|createdAt|updatedAt
```

This file is automatically updated by Executive Note CRUD functions.

## 7. Design theme

The requested green theme is used throughout the module:

- Primary: `#16A34A`
- Primary Dark: `#15803D`
- Accent: `#22C55E`
- Soft Accent: `#DCFCE7`
- Main Background: `#F8FAF9`
- Secondary Background: `#F0FDF4`
- Card Background: `#FFFFFF`
- Main Text: `#0F172A`
- Secondary Text: `#64748B`
- Border: `#DDE7E1`
- Warning: `#F59E0B`
- Danger: `#EF4444`

The interface includes responsive layouts, animated cards, hover states, chart animations, transitions, a responsive sidebar and management-focused dashboard styling.

## 8. Director access during development

In DEV mode the access filter supplies a Director role if no login session exists, so this standalone module can be demonstrated independently.

For integration with the full Hotel Workforce project, set the logged-in session role to `Director` and display name as required by your authentication module.

## 9. No SQL files are required

This project intentionally contains:

- no SQL schema,
- no SQL sample-data scripts,
- no JDBC URL,
- no SQL username/password,
- no Microsoft SQL Server driver,
- no H2 driver.

All persistent data used by this standalone module is stored as TXT files.
