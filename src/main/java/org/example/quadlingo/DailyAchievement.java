package org.example.quadlingo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_achievements")
public class DailyAchievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String type;

    private int target;

    private String icon;

    private LocalDate createdAt;

    public DailyAchievement() {}

    public DailyAchievement(String title, String type, int target, String icon, LocalDate createdAt) {
        this.title = title;
        this.type = type;
        this.target = target;
        this.icon = icon;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}