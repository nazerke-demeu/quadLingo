package org.example.quadlingo.repository;

import org.example.quadlingo.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findByDifficulty(String difficulty);
}