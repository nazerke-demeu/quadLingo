package org.example.alfaversion.controller;

import org.example.alfaversion.User;
import org.example.alfaversion.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public String getUserPage() {
        if (!userService.isUser()) {
            System.out.println("Redirecting to login: Not a USER role");
            return "redirect:/login";
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/user/profile")
    public String getUserProfile(Model model) {
        if (!userService.isUser()) {
            System.out.println("Redirecting to login: Not a USER role");
            return "redirect:/login";
        }
        User user = userService.getCurrentUser();
        if (user == null) {
            System.out.println("Redirecting to login: No user found in UserController");
            return "redirect:/login";
        }
        System.out.println("User found: " + user.getEmail() + ", Role: " + user.getRole());
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/user/profile")
    public String updateUserProfile(@RequestParam String name, @RequestParam String email, Model model) {
        if (!userService.isUser()) {
            System.out.println("Redirecting to login: Not a USER role");
            return "redirect:/login";
        }
        userService.updateUser(name, email);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("message", "Profile updated!");
        return "profile";
    }


}