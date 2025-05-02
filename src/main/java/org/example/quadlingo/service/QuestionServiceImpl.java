package org.example.quadlingo.service;

import org.example.quadlingo.Question;
import org.example.quadlingo.Quiz;
import org.example.quadlingo.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Override
    public void addQuestion(String text, String option1, String option2, String option3, String option4, Integer correctOption, Quiz quiz) {
        Question question = new Question();
        question.setText(text);
        question.setOption1(option1);
        question.setOption2(option2);
        question.setOption3(option3);
        question.setOption4(option4);
        question.setCorrectOption(correctOption);
        question.setQuiz(quiz);
        questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionsByQuiz(Quiz quiz) {
        return questionRepository.findByQuiz(quiz);
    }

    @Override
    public void updateQuestion(Integer id, String text, String option1, String option2, String option3, String option4, Integer correctOption) {
        questionRepository.findById(id).ifPresent(question -> {
            question.setText(text);
            question.setOption1(option1);
            question.setOption2(option2);
            question.setOption3(option3);
            question.setOption4(option4);
            question.setCorrectOption(correctOption);
            questionRepository.save(question);
        });
    }

    @Override
    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }
}