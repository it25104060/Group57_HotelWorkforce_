package com.goldenpalm.hotelworkforce.payroll.service.impl;

import com.goldenpalm.hotelworkforce.payroll.dto.PayrollRowDTO;
import com.goldenpalm.hotelworkforce.payroll.dto.PayrollSummaryDTO;
import com.goldenpalm.hotelworkforce.payroll.entity.Payroll;
import com.goldenpalm.hotelworkforce.payroll.entity.PayrollStatus;
import com.goldenpalm.hotelworkforce.payroll.repository.PayrollRepository;
import com.goldenpalm.hotelworkforce.payroll.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;

    @Autowired
    public PayrollServiceImpl(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
    }

    @Override
    public List<PayrollRowDTO> getPayrollForPeriod(String payPeriod) {
        return payrollRepository.findByPayPeriodOrderByEmployee_FullNameAsc(payPeriod)
                .stream()
                .map(this::toRowDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PayrollSummaryDTO getSummary(String payPeriod) {
        List<Payroll> records = payrollRepository.findByPayPeriodOrderByEmployee_FullNameAsc(payPeriod);

        double totalPayroll = records.stream().mapToDouble(Payroll::getNetPay).sum();
        double totalOvertimePay = records.stream().mapToDouble(Payroll::getOvertimePay).sum();
        long pending = records.stream().filter(r -> r.getStatus() == PayrollStatus.PENDING).count();
        long approved = records.stream().filter(r -> r.getStatus() == PayrollStatus.APPROVED).count();

        return new PayrollSummaryDTO(totalPayroll, totalOvertimePay, pending, approved, records.size());
    }

    @Override
    @Transactional
    public PayrollRowDTO approvePayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new NoSuchElementException("Payroll record not found: " + payrollId));

        // Proposal rule: recalculate before the record locks on approval.
        payroll.recalculateNetPay();
        payroll.approve();

        Payroll saved = payrollRepository.save(payroll);
        return toRowDTO(saved);
    }

    @Override
    @Transactional
    public List<PayrollRowDTO> approveAllPending(String payPeriod) {
        List<Payroll> pendingRecords = payrollRepository.findByPayPeriodAndStatus(payPeriod, PayrollStatus.PENDING);

        pendingRecords.forEach(record -> {
            record.recalculateNetPay();
            record.approve();
        });

        List<Payroll> saved = payrollRepository.saveAll(pendingRecords);
        return saved.stream().map(this::toRowDTO).collect(Collectors.toList());
    }

    @Override
    public String exportPeriodAsCsv(String payPeriod) {
        List<Payroll> records = payrollRepository.findByPayPeriodOrderByEmployee_FullNameAsc(payPeriod);

        StringBuilder csv = new StringBuilder();
        csv.append("Employee,Department,Base Pay,Overtime,Bonus,Deductions,Net Pay,Status\n");

        for (Payroll r : records) {
            csv.append(escapeCsv(r.getEmployee().getFullName())).append(",")
                    .append(escapeCsv(r.getEmployee().getDepartment())).append(",")
                    .append(r.getBasePay()).append(",")
                    .append(r.getOvertimePay()).append(",")
                    .append(r.getBonus()).append(",")
                    .append(r.getDeductions()).append(",")
                    .append(r.getNetPay()).append(",")
                    .append(r.getStatus()).append("\n");
        }
        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private PayrollRowDTO toRowDTO(Payroll p) {
        return new PayrollRowDTO(
                p.getId(),
                p.getEmployee().getId(),
                p.getEmployee().getFullName(),
                p.getEmployee().getDepartment(),
                p.getBasePay(),
                p.getOvertimePay(),
                p.getBonus(),
                p.getDeductions(),
                p.getNetPay(),
                p.getStatus().name()
        );
    }
}
