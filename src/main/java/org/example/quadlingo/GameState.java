package org.example.quadlingo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_state")
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int lives = 3;

    @Column
    private LocalDateTime lastLifeLost;

    @Column
    private String currentDifficulty;

    @Column
    private int questionsAnswered = 0;

    @Column(nullable = false)
    private int score = 0; // Добавляем поле для очков

    @Column
    private int quizzesCompleted = 0;
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }
    public void setQuizzesCompleted(int quizzesCompleted) {
        this.quizzesCompleted = quizzesCompleted;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getLives() {
        return lives;
    }
    public int getQuizzesCompleted() {
        return quizzesCompleted;
    }


    public void setLives(int lives) {
        this.lives = lives;
    }

    public LocalDateTime getLastLifeLost() {
        return lastLifeLost;
    }

    public void setLastLifeLost(LocalDateTime lastLifeLost) {
        this.lastLifeLost = lastLifeLost;
    }

    public String getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(String currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public int getQuestionsAnswered() {
        return questionsAnswered;
    }

    public void setQuestionsAnswered(int questionsAnswered) {
        this.questionsAnswered = questionsAnswered;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}