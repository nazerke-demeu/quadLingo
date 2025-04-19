package org.example.quadlingo.controller;
import org.example.quadlingo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin")
    public String getAdminPage() {
        if (!userService.isAdmin()) {
            return "redirect:/login";
        }
        return "redirect:/admin/profile";
    }

    @GetMapping("/admin/profile")
    public String getAdminProfile(Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/login";
        }
        model.addAttribute("name", userService.getCurrentUser().getName());
        model.addAttribute("email", userService.getCurrentUser().getEmail());
        return "admin_profile.html";
    }

    @PostMapping("/admin/profile")
    public String updateAdminProfile(@RequestParam String name, @RequestParam String email, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/login";
        }
        userService.updateUser(name, email);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("message", "Profile updated!");
        return "admin_profile.html";
    }
}