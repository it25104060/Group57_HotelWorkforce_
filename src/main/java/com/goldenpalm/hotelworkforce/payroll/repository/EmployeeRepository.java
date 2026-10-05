package com.goldenpalm.hotelworkforce.payroll.repository;

import com.goldenpalm.hotelworkforce.payroll.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Basic CRUD via Spring Data JPA. The full employee search/filter logic
 * (Section 6, Module 2 of the proposal) belongs in the Employee Directory module.
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
