package org.example.quadlingo.controller;

import org.example.quadlingo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String getLogin(Model model) {
        boolean loggedIn = userService.getCurrentUser() != null;
        model.addAttribute("loggedIn", loggedIn);
        if (loggedIn) {
            model.addAttribute("currentUserName", userService.getCurrentUser().name);
        }
        return "login.html";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        if (userService.findUser(email, password) != null) {
            return "redirect:/profile";
        }
        model.addAttribute("loggedIn", false);
        model.addAttribute("error", "Wrong email or password");
        return "login.html";
    }

    @PostMapping("/register")
    public String register(@RequestParam String name, @RequestParam String email,
                           @RequestParam String password, Model model) {
        userService.addUser(name, email, password);
        model.addAttribute("loggedIn", false);
        return "redirect:/login";
    }

    @PostMapping("/logout")
    public String logout() {
        userService.logout();
        return "redirect:/";
    }
}