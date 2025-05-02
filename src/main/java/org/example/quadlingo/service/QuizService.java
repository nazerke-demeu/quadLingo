package org.example.quadlingo.service;

import org.example.quadlingo.Lesson;
import org.example.quadlingo.Quiz;
import org.example.quadlingo.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    public Quiz addQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Optional<Quiz> getQuizById(Integer id) {
        return quizRepository.findById(id);
    }

    public void updateQuiz(Integer id, String title) {
        Optional<Quiz> optionalQuiz = quizRepository.findById(id);
        if (optionalQuiz.isPresent()) {
            Quiz quiz = optionalQuiz.get();
            quiz.setTitle(title);
            quizRepository.save(quiz);
        }
    }

    public void deleteQuiz(Integer id) {
        quizRepository.deleteById(id);
    }
}