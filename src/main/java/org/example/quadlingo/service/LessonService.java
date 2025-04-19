package org.example.quadlingo.service;

import org.example.quadlingo.Lesson;
import org.example.quadlingo.repository.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public void addLesson(String title, String description, String content) {
        Lesson lesson = new Lesson(title, description, content);
        lessonRepository.save(lesson);
    }

    public void deleteLesson(Integer id) {
        lessonRepository.deleteById(id);
    }

    public Optional<Lesson> getLessonById(Integer id) {
        return lessonRepository.findById(id);
    }

    public void updateLesson(Integer id, String title, String description, String content) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(id);
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            lesson.setTitle(title);
            lesson.setDescription(description);
            lesson.setContent(content);
            lessonRepository.save(lesson);
        }
    }
}