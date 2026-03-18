package com.sms.controller;

import com.sms.model.Fee;
import com.sms.model.Student;
import com.sms.repository.UserRepository;
import com.sms.service.FeeService;
import com.sms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * FeeController — handles fee record management and student fee views.
 */
@Controller
@RequestMapping("/fees")
public class FeeController {

    @Autowired private FeeService feeService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public String listFees(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("feeList", feeService.getAllFees());
        return "fees/list";
    }

    @GetMapping("/add")
    public String showAddForm(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("fee", new Fee());
        model.addAttribute("students", studentService.getAllStudents());
        return "fees/add";
    }

    @PostMapping("/add")
    public String addFee(@ModelAttribute Fee fee, RedirectAttributes redirectAttributes) {
        feeService.saveFee(fee);
        redirectAttributes.addFlashAttribute("successMessage", "Fee record added successfully!");
        return "redirect:/fees";
    }

    @GetMapping("/student/{id}")
    public String viewStudentFees(@PathVariable Integer id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        addUserInfo(auth, model);

        if (isStudentRole(auth) && !isOwnStudent(auth, id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only view your own fees.");
            return "redirect:/student-dashboard";
        }

        Student student = studentService.getStudentById(id).orElse(null);
        if (student == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Student not found.");
            return "redirect:/fees";
        }

        model.addAttribute("student", student);
        model.addAttribute("feeList", feeService.getFeesByStudentId(id));
        return "fees/view";
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
