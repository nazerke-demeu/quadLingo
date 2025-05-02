package org.example.quadlingo.controller;

import org.example.quadlingo.User;
import org.example.quadlingo.service.AchievementService;
import org.example.quadlingo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @Autowired
    private AchievementService achievementService;

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

        // Получаем состояние игры и прогресс ежедневных достижений
        GameState gameState = gameService.getGameState(user);
        if (gameState != null) {
            model.addAttribute("lives", gameState.getLives());
            model.addAttribute("score", gameState.getScore());
            model.addAttribute("quizzesCompleted", gameState.getQuizzesCompleted());
        } else {
            model.addAttribute("lives", 0);
            model.addAttribute("score", 0);
            model.addAttribute("quizzesCompleted", 0);
        }

        // Получаем прогресс ежедневных достижений
        achievementService.generateDailyAchievements();
        achievementService.initializeUserProgress(user);
        model.addAttribute("dailyProgress", achievementService.getUserProgress(user));

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

        // Повторно добавляем данные для отображения
        User user = userService.getCurrentUser();
        GameState gameState = gameService.getGameState(user);
        if (gameState != null) {
            model.addAttribute("lives", gameState.getLives());
            model.addAttribute("score", gameState.getScore());
            model.addAttribute("quizzesCompleted", gameState.getQuizzesCompleted());
        } else {
            model.addAttribute("lives", 0);
            model.addAttribute("score", 0);
            model.addAttribute("quizzesCompleted", 0);
        }
        model.addAttribute("dailyProgress", achievementService.getUserProgress(user));

        return "profile";
    }

    @PostMapping("/user/delete")
    public String deleteUserProfile() {
        if (!userService.isUser()) {
            return "redirect:/login";
        }
        userService.deleteUser();
        return "redirect:/";
    }
}