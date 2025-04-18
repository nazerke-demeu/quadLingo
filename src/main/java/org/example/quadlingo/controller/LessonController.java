package org.example.lingo.controller;

import org.example.lingo.service.LessonService;
import org.example.lingo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private UserService userService;

    // Show the learn page
    @GetMapping("/learn")
    public String getLearnPage(Model model) {
        if (userService.getCurrentUser() == null) {
            return "redirect:/login"; // Redirect to login if not logged in
        }
        model.addAttribute("lessons", lessonService.getAllLessons());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "learn.html";
    }

    // Handle lesson creation
    @PostMapping("/learn/add")
    public String addLesson(@RequestParam String title, @RequestParam String description, @RequestParam String content, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn"; // Only admins can add lessons
        }
        try {
            // Валидация входных данных
            if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                model.addAttribute("error", "Title, description, and content cannot be empty");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
            // Проверка длины полей
            if (title.length() > 255) {
                model.addAttribute("error", "Title must be 255 characters or less");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
            if (description.length() > 10000) {
                model.addAttribute("error", "Description must be 10,000 characters or less");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
            if (content.length() > 10000) {
                model.addAttribute("error", "Content must be 10,000 characters or less");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
            lessonService.addLesson(title, description, content);
            return "redirect:/learn";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add lesson: " + e.getMessage());
            model.addAttribute("lessons", lessonService.getAllLessons());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "learn.html";
        }
    }

    // Handle lesson deletion
    @PostMapping("/learn/delete")
    public String deleteLesson(@RequestParam Integer id) {
        if (!userService.isAdmin()) {
            return "redirect:/learn"; // Only admins can delete lessons
        }
        try {
            lessonService.deleteLesson(id);
        } catch (Exception e) {
            // Log error if needed, but redirect to avoid 500
        }
        return "redirect:/learn";
    }
}