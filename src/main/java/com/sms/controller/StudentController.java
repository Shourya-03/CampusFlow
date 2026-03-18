package com.sms.controller;

import com.sms.model.*;
import com.sms.repository.UserRepository;
import com.sms.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * StudentController — handles student CRUD, profiles, dashboards, and photo uploads.
 */
@Controller
public class StudentController {

    @Autowired private StudentService studentService;
    @Autowired private AttendanceService attendanceService;
    @Autowired private MarksService marksService;
    @Autowired private FeeService feeService;
    @Autowired private NoticeService noticeService;
    @Autowired private UserRepository userRepository;
    @Autowired private ReportService reportService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ===== ADMIN/TEACHER DASHBOARD =====

    @GetMapping("/dashboard")
    public String showDashboard(Authentication auth, Model model) throws Exception {
        addUserInfo(auth, model);

        // Stats
        model.addAttribute("totalStudents", studentService.getStudentCount());
        model.addAttribute("totalNotices", noticeService.getNoticeCount());

        // Latest notices
        String role = getRoleName(auth);
        model.addAttribute("latestNotices", noticeService.getLatestNoticesForRole(role, 3));

        // Charts data
        long above = reportService.getStudentsAboveAttendanceThreshold();
        long below = reportService.getStudentsBelowAttendanceThreshold();
        model.addAttribute("attendanceChartLabels", objectMapper.writeValueAsString(Arrays.asList("Above 75%", "Below 75%")));
        model.addAttribute("attendanceChartData", objectMapper.writeValueAsString(Arrays.asList(above, below)));

        Map<String, Double> avgBySubject = marksService.getAverageBySubject();
        model.addAttribute("marksChartLabels", objectMapper.writeValueAsString(new ArrayList<>(avgBySubject.keySet())));
        model.addAttribute("marksChartData", objectMapper.writeValueAsString(new ArrayList<>(avgBySubject.values())));

        return "dashboard";
    }

    // ===== STUDENT DASHBOARD =====

    @GetMapping("/student-dashboard")
    public String showStudentDashboard(Authentication auth, Model model) throws Exception {
        addUserInfo(auth, model);
        String role = getRoleName(auth);

        // Get student linked to this user
        Student student = getLinkedStudent(auth);
        if (student != null) {
            model.addAttribute("student", student);
            Integer sid = student.getStudentId();

            // Attendance chart
            List<Map<String, Object>> attSummary = attendanceService.getSubjectWiseSummary(sid);
            List<String> attLabels = new ArrayList<>();
            List<Double> attValues = new ArrayList<>();
            for (Map<String, Object> entry : attSummary) {
                attLabels.add((String) entry.get("subject"));
                attValues.add((Double) entry.get("percentage"));
            }
            model.addAttribute("attendanceChartLabels", objectMapper.writeValueAsString(attLabels));
            model.addAttribute("attendanceChartData", objectMapper.writeValueAsString(attValues));

            // Marks chart
            Map<String, Double> marksPcts = marksService.getSubjectPercentagesForStudent(sid);
            model.addAttribute("marksChartLabels", objectMapper.writeValueAsString(new ArrayList<>(marksPcts.keySet())));
            model.addAttribute("marksChartData", objectMapper.writeValueAsString(new ArrayList<>(marksPcts.values())));

            model.addAttribute("attendancePercentage", attendanceService.getOverallPercentage(sid));
            model.addAttribute("marksAverage", marksService.getAveragePercentage(sid));
        }

        // Latest notices
        model.addAttribute("latestNotices", noticeService.getLatestNoticesForRole(role, 3));

        return "student-dashboard";
    }

    // ===== STUDENT LIST =====

    @GetMapping("/students")
    public String listStudents(@RequestParam(value = "search", required = false) String keyword,
                               @RequestParam(value = "course", required = false) String course,
                               Authentication auth, Model model) {
        addUserInfo(auth, model);
        List<Student> students = studentService.searchStudents(keyword, course);
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCourse", course);
        model.addAttribute("courses", studentService.getAllCourses());
        model.addAttribute("resultCount", students.size());
        return "students/list";
    }

    // ===== ADD STUDENT =====

    @GetMapping("/students/add")
    public String showAddForm(Model model, Authentication auth) {
        addUserInfo(auth, model);
        model.addAttribute("student", new Student());
        return "students/add";
    }

    @PostMapping("/students/add")
    public String addStudent(@Valid @ModelAttribute Student student, BindingResult result,
                             Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addUserInfo(auth, model);
            return "students/add";
        }
        studentService.saveStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student added successfully!");
        return "redirect:/students";
    }

    // ===== EDIT STUDENT =====

    @GetMapping("/students/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, Authentication auth, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/students";
        }
        model.addAttribute("student", student.get());
        return "students/edit";
    }

    @PostMapping("/students/edit/{id}")
    public String editStudent(@PathVariable Integer id, @Valid @ModelAttribute Student student,
                              BindingResult result, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            addUserInfo(auth, model);
            student.setStudentId(id);
            return "students/edit";
        }
        student.setStudentId(id);
        // Preserve fields not in the form
        studentService.getStudentById(id).ifPresent(existing -> {
            student.setPhotoUrl(existing.getPhotoUrl());
            student.setUserId(existing.getUserId());
        });
        studentService.saveStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
        return "redirect:/students";
    }

    // ===== DELETE STUDENT =====

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        studentService.deleteStudent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        return "redirect:/students";
    }

    // ===== STUDENT PROFILE =====

    @GetMapping("/students/profile/{id}")
    public String showProfile(@PathVariable Integer id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);

        // STUDENT role can only view own profile
        if (isStudentRole(auth) && !isOwnProfile(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own profile.");
            return "redirect:/student-dashboard";
        }

        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/students";
        }

        Student student = studentOpt.get();
        model.addAttribute("student", student);
        model.addAttribute("attendancePercentage", attendanceService.getOverallPercentage(id));
        model.addAttribute("marksAverage", marksService.getAveragePercentage(id));

        // Fee status
        List<com.sms.model.Fee> fees = feeService.getFeesByStudentId(id);
        boolean allPaid = fees.stream().allMatch(f -> f.getStatus() == Fee.FeeStatus.PAID);
        boolean anyUnpaid = fees.stream().anyMatch(f -> f.getStatus() == Fee.FeeStatus.UNPAID);
        model.addAttribute("feeStatus", fees.isEmpty() ? "N/A" : allPaid ? "PAID" : anyUnpaid ? "UNPAID" : "PARTIALLY_PAID");

        // Recent records
        model.addAttribute("recentAttendance", attendanceService.getRecentAttendance(id, 10));
        model.addAttribute("recentMarks", marksService.getMarksByStudentId(id));

        return "students/profile";
    }

    @GetMapping("/students/profile/{id}/edit")
    public String showProfileEditForm(@PathVariable Integer id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);
        if (isStudentRole(auth) && !isOwnProfile(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit your own profile.");
            return "redirect:/student-dashboard";
        }
        Optional<Student> student = studentService.getStudentById(id);
        if (student.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/students";
        }
        model.addAttribute("student", student.get());
        return "students/edit";
    }

    @PostMapping("/students/profile/{id}/edit")
    public String editProfile(@PathVariable Integer id, @Valid @ModelAttribute Student student,
                              BindingResult result,
                              @RequestParam(value = "photo", required = false) MultipartFile photo,
                              Authentication auth, Model model, RedirectAttributes redirectAttributes) throws IOException {
        if (isStudentRole(auth) && !isOwnProfile(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit your own profile.");
            return "redirect:/student-dashboard";
        }
        if (result.hasErrors()) {
            addUserInfo(auth, model);
            student.setStudentId(id);
            return "students/edit";
        }

        student.setStudentId(id);
        // Preserve userId
        studentService.getStudentById(id).ifPresent(existing -> {
            student.setUserId(existing.getUserId());
            if (student.getPhotoUrl() == null || student.getPhotoUrl().isEmpty()) {
                student.setPhotoUrl(existing.getPhotoUrl());
            }
        });

        // Handle photo upload
        if (photo != null && !photo.isEmpty()) {
            String uploadDir = "src/main/resources/static/uploads/";
            Files.createDirectories(Paths.get(uploadDir));
            String filename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + filename);
            Files.write(filePath, photo.getBytes());
            student.setPhotoUrl(filename);
        }

        studentService.saveStudent(student);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/students/profile/" + id;
    }

    // ===== HELPER METHODS =====

    private void addUserInfo(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", getRoleName(auth));
    }

    private String getRoleName(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }

    private boolean isStudentRole(Authentication auth) {
        return getRoleName(auth).equals("STUDENT");
    }

    private boolean isOwnProfile(Authentication auth, Integer studentId) {
        Student linked = getLinkedStudent(auth);
        return linked != null && linked.getStudentId().equals(studentId);
    }

    private Student getLinkedStudent(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .flatMap(user -> studentService.getStudentByUserId(user.getUserId()))
                .orElse(null);
    }
}
