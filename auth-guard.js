(function () {
    const loggedInUser = sessionStorage.getItem("loggedInUser");
    const userRole = sessionStorage.getItem("userRole") || "System Admin";

    let currentPath = window.location.pathname.split("/").pop();
    if (!currentPath || currentPath === "") {
        currentPath = "index.html";
    }

    // 1. Redirect unauthenticated users to login page
    const publicPages = ["login.html", "splash.html"];

    if (!loggedInUser && !publicPages.includes(currentPath)) {
        window.location.href = "login.html";
        return;
    }

    // List of all valid system roles
    const allRoles = [
        "System Admin",
        "Admin",
        "HR Manager",
        "Finance Manager",
        "Department Head",
        "Department Manager",
        "Staff",
        "Employee",
        "IT Admin"
    ];

    // 2. Define page view permissions by role
    // IT Admin has access to ALL modules.
    const pagePermissions = {
        "index.html": allRoles,

        "user-access-administration.html": [
            "System Admin",
            "Admin",
            "IT Admin"
        ],

        "employee-profiles.html": [
            "System Admin",
            "Admin",
            "HR Manager",
            "Finance Manager",
            "Department Head",
            "Department Manager",
            "IT Admin"
        ],

        "attendance-time-tracking.html": allRoles,

        "leave-management.html": allRoles,

        "payroll-payslip-processing.html": [
            "System Admin",
            "Admin",
            "Finance Manager",
            "HR Manager",
            "IT Admin"
        ],

        "performance-management.html": [
            "System Admin",
            "Admin",
            "HR Manager",
            "Department Head",
            "Department Manager",
            "IT Admin"
        ],

        "reporting-analytics.html": [
            "System Admin",
            "Admin",
            "HR Manager",
            "Finance Manager",
            "Department Head",
            "Department Manager",
            "IT Admin"
        ],

        "my-profile.html": allRoles,

        "account-security.html": allRoles
    };

    // 3. Block direct URL access if role is unauthorized
    if (
        pagePermissions[currentPath] &&
        !pagePermissions[currentPath].includes(userRole)
    ) {
        alert(
            `Access Restricted: The ${userRole} role cannot access this module.`
        );

        window.location.href = "index.html";
        return;
    }

    // 4. Hide restricted links & dynamically update user details
    document.addEventListener("DOMContentLoaded", () => {

        const navMap = {
            'a[href*="user-access-administration.html"]': [
                "System Admin",
                "Admin",
                "IT Admin"
            ],

            'a[href*="employee-profiles.html"]': [
                "System Admin",
                "Admin",
                "HR Manager",
                "Finance Manager",
                "Department Head",
                "Department Manager",
                "IT Admin"
            ],

            'a[href*="payroll-payslip-processing.html"]': [
                "System Admin",
                "Admin",
                "Finance Manager",
                "HR Manager",
                "IT Admin"
            ],

            'a[href*="performance-management.html"]': [
                "System Admin",
                "Admin",
                "HR Manager",
                "Department Head",
                "Department Manager",
                "IT Admin"
            ],

            'a[href*="reporting-analytics.html"]': [
                "System Admin",
                "Admin",
                "HR Manager",
                "Finance Manager",
                "Department Head",
                "Department Manager",
                "IT Admin"
            ]
        };

        // Hide navigation links that the current role cannot access
        for (const [selector, allowedRoles] of Object.entries(navMap)) {

            const navLinks = document.querySelectorAll(selector);

            navLinks.forEach(navLink => {

                if (navLink && !allowedRoles.includes(userRole)) {

                    const parentLi = navLink.closest("li");

                    if (parentLi) {
                        parentLi.style.display = "none";
                    } else {
                        navLink.style.display = "none";
                    }
                }
            });
        }

        // Calculate initials
        const activeName = loggedInUser || "Active User";

        const initials =
            activeName !== "Active User" &&
            activeName.trim().length > 0
                ? activeName
                    .trim()
                    .split(" ")
                    .filter(n => n)
                    .map(n => n[0])
                    .join("")
                    .substring(0, 2)
                    .toUpperCase()
                : "AU";

        // Direct target: Top Navbar Profile Container
        const topProfile = document.querySelector(".top-user-profile");

        if (topProfile) {

            const nameEl =
                topProfile.querySelector("div > div:first-child");

            const roleEl =
                topProfile.querySelector("div > div:nth-child(2)");

            const avatarEl =
                topProfile.querySelector(".avatar");

            if (nameEl) {
                nameEl.textContent = activeName;
            }

            if (roleEl) {
                roleEl.textContent = userRole;
            }

            if (
                avatarEl &&
                !avatarEl.classList.contains("logo-box")
            ) {
                avatarEl.textContent = initials;
            }
        }

        // Direct target: Sidebar Footer Container
        const sidebarFooter =
            document.querySelector(".sidebar-footer");

        if (sidebarFooter) {

            const nameEl =
                sidebarFooter.querySelector(".user-name");

            const roleEl =
                sidebarFooter.querySelector(".user-role");

            const avatarEl =
                sidebarFooter.querySelector(".avatar");

            if (nameEl) {
                nameEl.textContent = activeName;
            }

            if (roleEl) {
                roleEl.textContent = userRole;
            }

            if (
                avatarEl &&
                !avatarEl.classList.contains("logo-box")
            ) {
                avatarEl.textContent = initials;
            }
        }

        // Update class-based user name elements
        document
            .querySelectorAll(
                ".user-name, .user-name-display, .profile-header-name, .user-card-name"
            )
            .forEach(el => {
                el.textContent = activeName;
            });

        // Update class-based role elements
        document
            .querySelectorAll(
                ".user-role, .profile-header-role"
            )
            .forEach(el => {
                el.textContent = userRole;
            });

        // Update avatar elements
        document
            .querySelectorAll(
                ".avatar, .large-avatar, .user-initials"
            )
            .forEach(el => {

                if (!el.classList.contains("logo-box")) {
                    el.textContent = initials;
                }
            });
    });

})();