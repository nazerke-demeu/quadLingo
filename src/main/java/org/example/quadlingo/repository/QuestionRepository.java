package org.example.quadlingo.repository;

import org.example.quadlingo.Question;
import org.example.quadlingo.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Question> findByQuiz(Quiz quiz);
}