package org.example.lingo.repository;


import org.example.lingo.DailyAchievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyAchievementRepository extends JpaRepository<DailyAchievement, Long> {
    boolean existsByCreatedAt(LocalDate createdAt);
    List<DailyAchievement> findByCreatedAt(LocalDate createdAt);
}