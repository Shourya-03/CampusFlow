package com.sms.controller;

import com.sms.model.Student;
import com.sms.model.Timetable;
import com.sms.repository.UserRepository;
import com.sms.service.StudentService;
import com.sms.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * TimetableController — handles timetable viewing (weekly grid) and management.
 */
@Controller
@RequestMapping("/timetable")
public class TimetableController {

    @Autowired private TimetableService timetableService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public String viewTimetable(@RequestParam(value = "course", required = false) String course,
                                Authentication auth, Model model) {
        addUserInfo(auth, model);

        String selectedCourse = course;
        // STUDENT: auto-filter by enrolled course
        if (isStudentRole(auth)) {
            Student student = getLinkedStudent(auth);
            if (student != null) {
                selectedCourse = student.getCourse();
            }
        }

        List<Timetable> entries;
        if (selectedCourse != null && !selectedCourse.isEmpty()) {
            entries = timetableService.getEntriesByCourse(selectedCourse);
        } else {
            entries = timetableService.getAllEntries();
        }

        model.addAttribute("entries", entries);
        model.addAttribute("selectedCourse", selectedCourse);
        model.addAttribute("courses", timetableService.getDistinctCourses());
        model.addAttribute("days", Timetable.DayOfWeek.values());
        return "timetable/list";
    }

    @GetMapping("/add")
    public String showAddForm(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("timetable", new Timetable());
        model.addAttribute("days", Timetable.DayOfWeek.values());
        return "timetable/add";
    }

    @PostMapping("/add")
    public String addEntry(@ModelAttribute Timetable timetable, RedirectAttributes redirectAttributes) {
        timetableService.saveEntry(timetable);
        redirectAttributes.addFlashAttribute("successMessage", "Timetable entry added successfully!");
        return "redirect:/timetable";
    }

    @PostMapping("/delete/{id}")
    public String deleteEntry(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        timetableService.deleteEntry(id);
        redirectAttributes.addFlashAttribute("successMessage", "Timetable entry deleted successfully!");
        return "redirect:/timetable";
    }

    private void addUserInfo(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", ""));
    }

    private boolean isStudentRole(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority().equals("ROLE_STUDENT");
    }

    private Student getLinkedStudent(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .flatMap(user -> studentService.getStudentByUserId(user.getUserId()))
                .orElse(null);
    }
}
