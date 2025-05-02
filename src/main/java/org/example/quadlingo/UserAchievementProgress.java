package org.example.quadlingo;

import jakarta.persistence.*;
@Entity
@Table(name = "user_achievements_progress")
public class UserAchievementProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private DailyAchievement achievement;

    private int progress;

    private boolean isCompleted;


    public UserAchievementProgress() {}

    public UserAchievementProgress(User user, DailyAchievement achievement, int progress, boolean isCompleted) {
        this.user = user;
        this.achievement = achievement;
        this.progress = progress;
        this.isCompleted = isCompleted;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DailyAchievement getAchievement() {
        return achievement;
    }

    public void setAchievement(DailyAchievement achievement) {
        this.achievement = achievement;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }



}