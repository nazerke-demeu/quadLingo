package org.example.quadlingo.controller;

import org.example.quadlingo.User;
import org.example.quadlingo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String getProfile(Model model) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("name", user.name);
        model.addAttribute("email", user.email);
        return "profile.html";
    }
    @PostMapping("/profile")
    public String updateProfile(@RequestParam String name, @RequestParam String email, Model model) {
        userService.updateUser(name, email);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("message", "Profile updated!");
        return "profile.html";
    }
}