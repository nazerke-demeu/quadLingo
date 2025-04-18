package org.example.quadlingo.service;

import org.example.quadlingo.Lesson;
import org.example.lingo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    public void addLesson(String title, String description, String content) {
        Lesson lesson = new Lesson(title, description, content);
        lessonRepository.save(lesson);
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public void deleteLesson(Integer id) {
        lessonRepository.deleteById(id);
    }
}