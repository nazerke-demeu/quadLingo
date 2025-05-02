package org.example.quadlingo.service;

import org.example.quadlingo.Question;
import org.example.quadlingo.Quiz;

import java.util.List;

public interface QuestionService {
    void addQuestion(String text, String option1, String option2, String option3, String option4, Integer correctOption, Quiz quiz);
    List<Question> getQuestionsByQuiz(Quiz quiz);
    void updateQuestion(Integer id, String text, String option1, String option2, String option3, String option4, Integer correctOption);
    void deleteQuestion(Integer id);
}