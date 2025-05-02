package org.example.quadlingo.repository;

import org.example.quadlingo.GameState;
import org.example.quadlingo.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameStateRepository extends JpaRepository<GameState, Long> {
    List<GameState> findTop10ByOrderByScoreDesc();

    GameState findByUser(User user);
}