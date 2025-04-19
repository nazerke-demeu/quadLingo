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
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/user")
    public String getUserPage() {
        if (!userService.isUser()) {
            return "redirect:/login";
        }
        return "redirect:/user/profile";
    }

    @GetMapping("/user/profile")
    public String getUserProfile(Model model) {
        User user = userService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        return "profile";
    }

    @PostMapping("/user/profile")
    public String updateUserProfile(@RequestParam String name, @RequestParam String email, Model model) {
        if (!userService.isUser()) {
            return "redirect:/login";
        }
        userService.updateUser(name, email);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("message", "Profile updated!");
        return "profile";
    }
}