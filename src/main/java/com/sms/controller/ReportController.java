package com.sms.controller;

import com.sms.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ReportController — admin-only reports dashboard with aggregated statistics.
 */
@Controller
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports")
    public String showReports(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));

        model.addAttribute("totalStudents", reportService.getTotalStudents());
        model.addAttribute("totalNotices", reportService.getTotalNotices());
        model.addAttribute("totalFeeCollected", reportService.getTotalFeeCollected());
        model.addAttribute("totalFeePending", reportService.getTotalFeePending());
        model.addAttribute("belowAttendance", reportService.getStudentsBelowAttendanceThreshold());
        model.addAttribute("topStudents", reportService.getTopStudentsByMarks(5));
        model.addAttribute("bottomAttendance", reportService.getBottomStudentsByAttendance(5));

        return "reports/dashboard";
    }
}
