package org.example.quadlingo.controller;

import org.example.quadlingo.User;
import org.example.quadlingo.Word;
import org.example.quadlingo.service.GameService;
import org.example.quadlingo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
public class GameController {

    private static final Logger log = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private GameService gameService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String game(Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }
            model.addAttribute("gameState", gameService.getGameState(currentUser));
            model.addAttribute("canPlay", gameService.canPlayGame(currentUser));
            if (!gameService.canPlayGame(currentUser)) {
                model.addAttribute("remainingSeconds", gameService.getRemainingSeconds(currentUser));
            }
            return "mini_game";
        } catch (Exception e) {
            log.error("Error loading game page", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "mini_game";
        }
    }

    @GetMapping("/start")
    public String startGame(@RequestParam String difficulty, Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }
            if (!gameService.canPlayGame(currentUser)) {
                model.addAttribute("gameState", gameService.getGameState(currentUser));
                model.addAttribute("canPlay", false);
                model.addAttribute("remainingSeconds", gameService.getRemainingSeconds(currentUser));
                return "mini_game";
            }
            Word word = gameService.getRandomWord(difficulty);
            model.addAttribute("word", word);
            model.addAttribute("gameState", gameService.getGameState(currentUser));
            model.addAttribute("questionsAnswered", gameService.getGameState(currentUser).getQuestionsAnswered());
            model.addAttribute("canPlay", true);
            model.addAttribute("score", 10); // Фиксированные очки за правильный ответ
            return "mini_game";
        } catch (Exception e) {
            log.error("Error starting game", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("canPlay", false);
            return "mini_game";
        }
    }

    @PostMapping("/answer")
    public String answer(@RequestParam Long wordId, @RequestParam String answer, @RequestParam int score, Model model) {
        try {
            log.info("Processing answer: wordId={}, answer={}, score={}", wordId, answer, score);
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }
            boolean isCorrect = gameService.checkAnswer(currentUser, wordId, answer, score);
            if (gameService.isGameOver(gameService.getGameState(currentUser))) {
                model.addAttribute("gameOver", true);
                model.addAttribute("score", gameService.getGameState(currentUser).getScore());
                model.addAttribute("gameState", gameService.getGameState(currentUser));
                return "mini_game";
            }
            if (!isCorrect && gameService.getGameState(currentUser).getLives() <= 0) {
                model.addAttribute("gameState", gameService.getGameState(currentUser));
                model.addAttribute("canPlay", false);
                model.addAttribute("remainingSeconds", gameService.getRemainingSeconds(currentUser));
                return "mini_game";
            }
            Word nextWord = gameService.getRandomWord(gameService.getGameState(currentUser).getCurrentDifficulty());
            model.addAttribute("word", nextWord);
            model.addAttribute("gameState", gameService.getGameState(currentUser));
            model.addAttribute("questionsAnswered", gameService.getGameState(currentUser).getQuestionsAnswered());
            model.addAttribute("canPlay", true);
            model.addAttribute("score", 10); // Фиксированные очки для следующего вопроса
            return "mini_game";
        } catch (IllegalArgumentException e) {
            log.error("Invalid input parameters: wordId={}, answer={}, score={}", wordId, answer, score, e);
            model.addAttribute("error", "Invalid input: " + e.getMessage());
            model.addAttribute("gameState", gameService.getGameState(userService.getCurrentUser()));
            model.addAttribute("canPlay", false);
            return "mini_game";
        } catch (Exception e) {
            log.error("Error processing answer", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("gameState", gameService.getGameState(userService.getCurrentUser()));
            model.addAttribute("canPlay", false);
            return "mini_game";
        }
    }
}