package org.example.quadlingo.service;

import org.example.lingo.Lesson;
import org.example.lingo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    // Add a new lesson
    public void addLesson(String title, String description, String content) {
        Lesson lesson = new Lesson(title, description, content);
        lessonRepository.save(lesson);
    }

    // Get all lessons
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    // Delete a lesson by ID
    public void deleteLesson(Integer id) {
        lessonRepository.deleteById(id);
    }
}