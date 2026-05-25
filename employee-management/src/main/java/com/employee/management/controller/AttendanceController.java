package com.employee.management.controller;

import com.employee.management.dto.ClockInRequest;
import com.employee.management.dto.ClockOutRequest;
import com.employee.management.entity.AttendanceLog;
import com.employee.management.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/clock-in")
    public AttendanceLog clockIn(

            @RequestBody ClockInRequest request
    ) {
        return attendanceService.clockIn(request);
    }

    @PostMapping("/clock-out")
    public AttendanceLog clockOut(
            @RequestBody ClockOutRequest request
    ) {
        return attendanceService.clockOut(request);
    }

    @GetMapping("/log")
    public Page<AttendanceLog> getLogs(
            @RequestParam Long workerId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {

        return attendanceService.getAttendanceLogs(
                workerId,
                from,
                to,
                page,
                size
        );
    }
}