Hotel Workforce - Reporting & Executive Analytics TXT Storage

This folder is the complete storage layer for this standalone module.
No database is used.

employees.txt             -> workforce source data
attendance_records.txt    -> attendance source data
leave_requests.txt        -> leave source data
payroll_records.txt       -> payroll source data
report_definitions.txt    -> full CRUD storage for saved executive reports
executive_notes.txt       -> full CRUD storage for executive notes

Lines beginning with # are headers/comments.
Fields are separated with | characters.
The application escapes special characters automatically when it writes records.
