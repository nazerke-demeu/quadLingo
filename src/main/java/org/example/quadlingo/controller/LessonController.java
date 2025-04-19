package org.example.quadlingo.controller;

import org.example.quadlingo.service.LessonService;
import org.example.quadlingo.service.UserService;
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

    @GetMapping("/learn")
    public String getLearnPage(Model model) {
        if (userService.getCurrentUser() == null) {
            return "redirect:/login";
        }
        model.addAttribute("lessons", lessonService.getAllLessons());
        model.addAttribute("isAdmin", userService.isAdmin());
        return "learn.html";
    }

    @PostMapping("/learn/add")
    public String addLesson(@RequestParam String title, @RequestParam String description, @RequestParam String content, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                model.addAttribute("error", "Title, description, and content cannot be empty");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
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

    @GetMapping("/learn/edit")
    public String showEditLessonForm(@RequestParam Integer id, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            var lessonOptional = lessonService.getLessonById(id);
            if (lessonOptional.isPresent()) {
                model.addAttribute("editLesson", lessonOptional.get());
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            } else {
                model.addAttribute("error", "Lesson not found");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load lesson: " + e.getMessage());
            model.addAttribute("lessons", lessonService.getAllLessons());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "learn.html";
        }
    }

    @PostMapping("/learn/edit")
    public String editLesson(@RequestParam Integer id, @RequestParam String title, @RequestParam String description, @RequestParam String content, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty() || content == null || content.trim().isEmpty()) {
                model.addAttribute("error", "Title, description, and content cannot be empty");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn.html";
            }
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
            lessonService.updateLesson(id, title, description, content);
            return "redirect:/learn";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit lesson: " + e.getMessage());
            model.addAttribute("lessons", lessonService.getAllLessons());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "learn.html";
        }
    }

    @PostMapping("/learn/delete")
    public String deleteLesson(@RequestParam Integer id) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.deleteLesson(id);
        } catch (Exception e) {
        }
        return "redirect:/learn";
    }
}