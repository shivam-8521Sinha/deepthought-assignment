package com.employee.management.repository;

import com.employee.management.entity.OvertimeEntry;
import com.employee.management.enums.SettlementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OvertimeEntryRepository extends JpaRepository<OvertimeEntry, Long> {

    List<OvertimeEntry> findByWorkerIdAndDateBetween(
            Long workerId,
            LocalDate start,
            LocalDate end
    );

    List<OvertimeEntry> findByWorkerIdAndDateBetweenAndSettlementStatus(
            Long workerId,
            LocalDate start,
            LocalDate end,
            SettlementStatus settlementStatus
    );
}