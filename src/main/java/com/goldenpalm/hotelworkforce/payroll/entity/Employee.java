package com.goldenpalm.hotelworkforce.payroll.entity;

import jakarta.persistence.*;

/**
 * Minimal Employee entity needed by the Payroll module.
 * The full Employee Directory (CRUD, NIC validation, etc.) belongs to Module 2
 * of the proposal and should live in its own package — this is just enough
 * for Payroll to reference an employee and read their department/base salary.
 */
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String department;

    @Column(unique = true, nullable = false)
    private String nic;

    private String designation;

    @Column(name = "base_salary")
    private Double baseSalary;

    @Column(nullable = false)
    private Boolean active = true;

    public Employee() {
    }

    public Employee(String fullName, String department, String nic, String designation, Double baseSalary) {
        this.fullName = fullName;
        this.department = department;
        this.nic = nic;
        this.designation = designation;
        this.baseSalary = baseSalary;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(Double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
