package org.example.quadlingo.service;

import org.example.quadlingo.DailyAchievement;
import org.example.quadlingo.User;
import org.example.quadlingo.UserAchievementProgress;
import org.example.quadlingo.repository.DailyAchievementRepository;
import org.example.quadlingo.repository.UserAchievementProgressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AchievementService {

    private static final Logger log = LoggerFactory.getLogger(AchievementService.class);

    @Autowired
    private DailyAchievementRepository dailyAchievementRepository;

    @Autowired
    private UserAchievementProgressRepository userAchievementProgressRepository;

    @Autowired
    private UserService userService;

    private List<AchievementTemplate> getPredefinedTemplates() {
        List<AchievementTemplate> templates = new ArrayList<>();
        templates.add(new AchievementTemplate("Answer correctly 5 times", "correct_answers", 5, "🎯"));
        templates.add(new AchievementTemplate("Complete 2 quizzes", "games_played", 2, "🎮"));
        templates.add(new AchievementTemplate("Score 50 points", "score_earned", 50, "🏆"));
        templates.add(new AchievementTemplate("Answer correctly 10 times", "correct_answers", 10, "🎯"));
        templates.add(new AchievementTemplate("Complete 3 quizzes", "games_played", 3, "🎮"));
        return templates;
    }

    public void generateDailyAchievements() {
        LocalDate today = LocalDate.now();
        if (!dailyAchievementRepository.existsByCreatedAt(today)) {
            log.info("Generating daily achievements for {}", today);
            List<AchievementTemplate> templates = getPredefinedTemplates();
            Collections.shuffle(templates);
            List<AchievementTemplate> todays = templates.subList(0, Math.min(3, templates.size())); // Выбираем 3 случайных
            for (AchievementTemplate t : todays) {
                DailyAchievement achievement = new DailyAchievement(t.getTitle(), t.getType(), t.getTarget(), t.getIcon(), today);
                dailyAchievementRepository.save(achievement);
                log.debug("Created daily achievement: {}", t.getTitle());
            }
        }
    }

    public void initializeUserProgress(User user) {
        if (userService.isAdmin()) {
            return;
        }
        LocalDate today = LocalDate.now();
        List<DailyAchievement> todaysAchievements = dailyAchievementRepository.findByCreatedAt(today);
        List<UserAchievementProgress> userProgress = userAchievementProgressRepository.findByUser(user);

        for (DailyAchievement achievement : todaysAchievements) {
            boolean alreadyExists = userProgress.stream()
                    .anyMatch(p -> p.getAchievement().getId().equals(achievement.getId()));
            if (!alreadyExists) {
                UserAchievementProgress progress = new UserAchievementProgress(user, achievement, 0, false);
                userAchievementProgressRepository.save(progress);
                log.debug("Initialized progress for user {} and achievement {}", user.getEmail(), achievement.getTitle());
            }
        }
    }

    public void handleCorrectAnswer(User user) {
        if (userService.isAdmin()) {
            return;
        }
        List<UserAchievementProgress> progressList = userAchievementProgressRepository.findByUserAndIsCompletedFalse(user);
        for (UserAchievementProgress progress : progressList) {
            DailyAchievement ach = progress.getAchievement();
            if ("correct_answers".equals(ach.getType())) {
                progress.setProgress(progress.getProgress() + 1);
                if (progress.getProgress() >= ach.getTarget()) {
                    progress.setCompleted(true);
                    log.info("User {} completed daily achievement: {}", user.getEmail(), ach.getTitle());
                }
                userAchievementProgressRepository.save(progress);
            }
        }
    }

    public void handleQuizCompleted(User user) {
        if (userService.isAdmin()) {
            return;
        }
        List<UserAchievementProgress> progressList = userAchievementProgressRepository.findByUserAndIsCompletedFalse(user);
        for (UserAchievementProgress progress : progressList) {
            DailyAchievement ach = progress.getAchievement();
            if ("games_played".equals(ach.getType())) {
                progress.setProgress(progress.getProgress() + 1);
                if (progress.getProgress() >= ach.getTarget()) {
                    progress.setCompleted(true);
                    log.info("User {} completed daily achievement: {}", user.getEmail(), ach.getTitle());
                }
                userAchievementProgressRepository.save(progress);
            }
        }
    }

    public void handleScoreEarned(User user, int scoreIncrement) {
        if (userService.isAdmin()) {
            return;
        }
        List<UserAchievementProgress> progressList = userAchievementProgressRepository.findByUserAndIsCompletedFalse(user);
        for (UserAchievementProgress progress : progressList) {
            DailyAchievement ach = progress.getAchievement();
            if ("score_earned".equals(ach.getType())) {
                progress.setProgress(progress.getProgress() + scoreIncrement);
                if (progress.getProgress() >= ach.getTarget()) {
                    progress.setCompleted(true);
                    log.info("User {} completed daily achievement: {}", user.getEmail(), ach.getTitle());
                }
                userAchievementProgressRepository.save(progress);
            }
        }
    }

    public List<UserAchievementProgress> getUserProgress(User user) {
        return userAchievementProgressRepository.findByUser(user);
    }

    public boolean areAllDailyAchievementsCompleted(User user) {
        LocalDate today = LocalDate.now();
        List<DailyAchievement> todaysAchievements = dailyAchievementRepository.findByCreatedAt(today);
        List<UserAchievementProgress> userProgress = userAchievementProgressRepository.findByUser(user);

        List<UserAchievementProgress> todaysProgress = userProgress.stream()
                .filter(progress -> progress.getAchievement().getCreatedAt().equals(today))
                .toList();

        return todaysProgress.size() == todaysAchievements.size() && todaysProgress.stream().allMatch(UserAchievementProgress::isCompleted);
    }
}

class AchievementTemplate {
    private String title;
    private String type;
    private int target;
    private String icon;

    public AchievementTemplate(String title, String type, int target, String icon) {
        this.title = title;
        this.type = type;
        this.target = target;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getTarget() {
        return target;
    }

    public String getIcon() {
        return icon;
    }
}