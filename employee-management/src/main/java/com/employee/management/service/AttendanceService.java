package com.employee.management.service;

import com.employee.management.dto.ClockInRequest;
import com.employee.management.dto.ClockOutRequest;
import com.employee.management.entity.AttendanceLog;
import com.employee.management.entity.Site;
import com.employee.management.entity.Worker;
import com.employee.management.exception.CustomException;
import com.employee.management.repository.AttendanceLogRepository;
import com.employee.management.repository.SiteRepository;
import com.employee.management.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class AttendanceService {

    @Autowired
    private WorkerRepository workerRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private AttendanceLogRepository attendanceLogRepository;

    public AttendanceLog clockIn(ClockInRequest request) {
//        System.out.println(request.getWorkerId());
//        System.out.println(workerRepository.findAll());
        Worker worker = workerRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new CustomException(
                        "WORKER_NOT_FOUND",
                        "Worker not found"
                ));

        if (!worker.getActive()) {
            throw new CustomException(
                    "WORKER_INACTIVE",
                    "Worker is inactive"
            );
        }

        Site site = siteRepository.findById(request.getSiteId())
                .orElseThrow(() -> new CustomException(
                        "SITE_NOT_FOUND",
                        "Site not found"
                ));

        if (!site.getActive()) {
            throw new CustomException(
                    "SITE_INACTIVE",
                    "Site inactive"
            );
        }

        attendanceLogRepository
                .findByWorkerIdAndClockOutIsNull(worker.getId())
                .ifPresent(a -> {
                    throw new CustomException(
                            "DUPLICATE_CLOCK_IN",
                            "Worker already clocked in"
                    );
                });

        AttendanceLog log = new AttendanceLog();

        log.setWorker(worker);
        log.setSite(site);
        log.setClockIn(LocalDateTime.now());

        return attendanceLogRepository.save(log);
    }

    public AttendanceLog clockOut(ClockOutRequest request) {

        AttendanceLog log = attendanceLogRepository
                .findByWorkerIdAndClockOutIsNull(request.getWorkerId())
                .orElseThrow(() -> new CustomException(
                        "ACTIVE_ATTENDANCE_NOT_FOUND",
                        "Worker not clocked in"
                ));

        LocalDateTime now = LocalDateTime.now();

        log.setClockOut(now);

        double hours =
                Duration.between(log.getClockIn(), now).toMinutes() / 60.0;

        log.setTotalHoursWorked(hours);

        double overtime = Math.max(0, hours - 8);

        if (overtime > 60) {
            overtime = 60;
        }

        log.setOvertimeHours(overtime);

        if (hours > 16) {
            log.setFlagged(true);
        }

        return attendanceLogRepository.save(log);
    }

    public Page<AttendanceLog> getAttendanceLogs(
            Long workerId,
            LocalDateTime from,
            LocalDateTime to,
            int page,
            int size
    ) {

        return attendanceLogRepository
                .findByWorkerIdAndClockInBetween(
                        workerId,
                        from,
                        to,
                        PageRequest.of(page, size)
                );
    }
}