package org.example.quadlingo.service;

import org.example.quadlingo.GameState;
import org.example.quadlingo.User;
import org.example.quadlingo.Word;
import org.example.quadlingo.repository.GameStateRepository;
import org.example.quadlingo.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private GameStateRepository gameStateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AchievementService achievementService;

    private static final int SCORE_PER_CORRECT_ANSWER = 10;

    public Word getRandomWord(String difficulty) {
        List<Word> words = wordRepository.findByDifficulty(difficulty.toUpperCase());
        if (words.isEmpty()) {
            throw new RuntimeException("No words available for difficulty: " + difficulty);
        }
        return words.get(new Random().nextInt(words.size()));
    }

    public GameState getGameState(User user) {
        GameState state = gameStateRepository.findByUser(user);
        if (state == null) {
            state = new GameState();
            state.setUser(user);
            state.setLives(3);
            state.setScore(0);
            state.setQuestionsAnswered(0);
            state.setCurrentDifficulty("EASY");
            gameStateRepository.save(state);
        }
        if (state.getLives() < 3 && state.getLastLifeLost() != null) {
            long minutesSinceLastLoss = ChronoUnit.MINUTES.between(state.getLastLifeLost(), LocalDateTime.now());
            if (minutesSinceLastLoss >= 5 && state.getLives() == 0) {
                state.setLives(1);
                state.setLastLifeLost(null);
                state.setQuestionsAnswered(0);
                gameStateRepository.save(state);
            }
        }
        return state;
    }

    public boolean checkAnswer(User user, Long wordId, String answer, int score) {
        Word word = wordRepository.findById(wordId)
                .orElseThrow(() -> new RuntimeException("Word not found"));
        GameState state = getGameState(user);
        boolean isCorrect = word.getKazakh().equalsIgnoreCase(answer.trim());
        if (!isCorrect) {
            state.setLives(state.getLives() - 1);
            state.setLastLifeLost(LocalDateTime.now());
        } else {
            state.setScore(state.getScore() + SCORE_PER_CORRECT_ANSWER);

            if (!userService.isAdmin()) {
                achievementService.handleCorrectAnswer(user);
                achievementService.handleScoreEarned(user, SCORE_PER_CORRECT_ANSWER);
            }
        }
        state.setQuestionsAnswered(state.getQuestionsAnswered() + 1);
        gameStateRepository.save(state);
        return isCorrect;
    }

    public boolean checkQuizAnswer(User user, boolean isCorrect) {
        GameState state = getGameState(user);
        if (!isCorrect) {
            state.setLives(state.getLives() - 1);
            state.setLastLifeLost(LocalDateTime.now());
        } else {
            state.setScore(state.getScore() + SCORE_PER_CORRECT_ANSWER);

            if (!userService.isAdmin()) {
                achievementService.handleCorrectAnswer(user);
                achievementService.handleScoreEarned(user, SCORE_PER_CORRECT_ANSWER);
            }
        }
        gameStateRepository.save(state);
        return isCorrect;
    }

    public void incrementQuizzesCompleted(User user) {
        GameState state = getGameState(user);
        if (state == null) {
            log.error("Cannot increment quizzes completed: game state is null for user {}", user.getEmail());
            return;
        }
        state.setQuizzesCompleted(state.getQuizzesCompleted() + 1);
        gameStateRepository.save(state);

        if (!userService.isAdmin()) {
            achievementService.handleQuizCompleted(user);
        }
    }

    public boolean isGameOver(GameState state) {
        return state.getLives() <= 0 || state.getQuestionsAnswered() >= 10;
    }

    public void resetGame(User user) {
        GameState state = getGameState(user);
        state.setLives(3);
        state.setLastLifeLost(null);
        state.setQuestionsAnswered(0);
        gameStateRepository.save(state);
    }

    public boolean canPlayGame(User user) {
        GameState state = getGameState(user);
        return state.getLives() > 0;
    }

    public long getRemainingSeconds(User user) {
        GameState state = getGameState(user);
        if (state.getLives() > 0 || state.getLastLifeLost() == null) {
            return 0;
        }
        long secondsSinceLastLoss = ChronoUnit.SECONDS.between(state.getLastLifeLost(), LocalDateTime.now());
        return Math.max(0, 5 * 60 - secondsSinceLastLoss);
    }
}