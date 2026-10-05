package com.goldenpalm.hotelworkforce.payroll.repository;

import com.goldenpalm.hotelworkforce.payroll.entity.Payroll;
import com.goldenpalm.hotelworkforce.payroll.entity.PayrollStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    /** Powers the payroll register table for a given pay period, sorted by employee name. */
    List<Payroll> findByPayPeriodOrderByEmployee_FullNameAsc(String payPeriod);

    /** Used by "Approve batch" to fetch only the rows still pending in this period. */
    List<Payroll> findByPayPeriodAndStatus(String payPeriod, PayrollStatus status);

    Optional<Payroll> findByEmployee_IdAndPayPeriod(Long employeeId, String payPeriod);

    long countByPayPeriodAndStatus(String payPeriod, PayrollStatus status);
}
