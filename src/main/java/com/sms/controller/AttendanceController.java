package com.sms.controller;

import com.sms.model.Attendance;
import com.sms.model.Student;
import com.sms.repository.UserRepository;
import com.sms.service.AttendanceService;
import com.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * AttendanceController — handles attendance marking and viewing.
 */
@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public String listAttendance(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("attendanceList", attendanceService.getAllAttendance());
        return "attendance/list";
    }

    @GetMapping("/mark")
    public String showMarkForm(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("subjects", attendanceService.getAllSubjects());
        return "attendance/mark";
    }

    @PostMapping("/mark")
    public String markAttendance(@ModelAttribute Attendance attendance, RedirectAttributes redirectAttributes) {
        attendanceService.saveAttendance(attendance);
        redirectAttributes.addFlashAttribute("successMessage", "Attendance marked successfully!");
        return "redirect:/attendance";
    }

    @GetMapping("/student/{id}")
    public String viewStudentAttendance(@PathVariable Integer id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);

        // STUDENT role can only view own
        if (isStudentRole(auth) && !isOwnStudent(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own attendance.");
            return "redirect:/student-dashboard";
        }

        Student student = studentService.getStudentById(id).orElse(null);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/attendance";
        }

        model.addAttribute("student", student);
        model.addAttribute("summary", attendanceService.getSubjectWiseSummary(id));
        model.addAttribute("overallPercentage", attendanceService.getOverallPercentage(id));
        return "attendance/view";
    }

    private void addUserInfo(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }

    private boolean isStudentRole(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority().equals("ROLE_STUDENT");
    }

    private boolean isOwnStudent(Authentication auth, Integer studentId) {
        return userRepository.findByUsername(auth.getName())
                .flatMap(user -> studentService.getStudentByUserId(user.getUserId()))
                .map(s -> s.getStudentId().equals(studentId))
                .orElse(false);
    }
}
