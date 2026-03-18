package com.sms.controller;

import com.sms.model.Marks;
import com.sms.model.Student;
import com.sms.repository.UserRepository;
import com.sms.service.MarksService;
import com.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MarksController — handles marks entry and student result views.
 */
@Controller
@RequestMapping("/marks")
public class MarksController {

    @Autowired private MarksService marksService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public String listMarks(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("marksList", marksService.getAllMarks());
        return "marks/list";
    }

    @GetMapping("/add")
    public String showAddForm(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("marks", new Marks());
        model.addAttribute("students", studentService.getAllStudents());
        return "marks/add";
    }

    @PostMapping("/add")
    public String addMarks(@ModelAttribute Marks marks, RedirectAttributes redirectAttributes) {
        // Validate marks <= max
        if (marks.getMarksObtained() != null && marks.getMaxMarks() != null &&
            marks.getMarksObtained().compareTo(marks.getMaxMarks()) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Marks obtained cannot exceed max marks!");
            return "redirect:/marks/add";
        }
        marksService.saveMarks(marks);
        redirectAttributes.addFlashAttribute("successMessage", "Marks recorded successfully!");
        return "redirect:/marks";
    }

    @GetMapping("/student/{id}")
    public String viewStudentMarks(@PathVariable Integer id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);

        if (isStudentRole(auth) && !isOwnStudent(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own marks.");
            return "redirect:/student-dashboard";
        }

        Student student = studentService.getStudentById(id).orElse(null);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/marks";
        }

        model.addAttribute("student", student);
        model.addAttribute("marksList", marksService.getMarksByStudentId(id));
        model.addAttribute("averagePercentage", marksService.getAveragePercentage(id));
        model.addAttribute("overallGrade", marksService.getOverallGrade(id));
        return "marks/view";
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
