package org.example.quadlingo.repository;

import org.example.quadlingo.User;
import org.example.quadlingo.UserAchievementProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAchievementProgressRepository extends JpaRepository<UserAchievementProgress, Long> {
    List<UserAchievementProgress> findByUserAndIsCompletedFalse(User user);
    List<UserAchievementProgress> findByUser(User user);
}