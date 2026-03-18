package com.sms.controller;

import com.sms.model.Notice;
import com.sms.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * NoticeController — handles notice board CRUD and role-based visibility.
 */
@Controller
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public String listNotices(Authentication auth, Model model) {
        addUserInfo(auth, model);
        String role = getRoleName(auth);
        model.addAttribute("notices", noticeService.getNoticesForRole(role));
        return "notices/list";
    }

    @GetMapping("/add")
    public String showAddForm(Authentication auth, Model model) {
        addUserInfo(auth, model);
        model.addAttribute("notice", new Notice());
        return "notices/add";
    }

    @PostMapping("/add")
    public String addNotice(@ModelAttribute Notice notice, Authentication auth, RedirectAttributes redirectAttributes) {
        notice.setPostedBy(auth.getName());
        notice.setPostedAt(LocalDateTime.now());
        noticeService.saveNotice(notice);
        redirectAttributes.addFlashAttribute("successMessage", "Notice posted successfully!");
        return "redirect:/notices";
    }

    @PostMapping("/delete/{id}")
    public String deleteNotice(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        noticeService.deleteNotice(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notice deleted successfully!");
        return "redirect:/notices";
    }

    private void addUserInfo(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", getRoleName(auth));
    }

    private String getRoleName(Authentication auth) {
        return auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
    }
}
