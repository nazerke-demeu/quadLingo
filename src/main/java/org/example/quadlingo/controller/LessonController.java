package org.example.quadlingo.controller;

import org.example.quadlingo.*;
import org.example.quadlingo.service.LessonService;
import org.example.quadlingo.service.QuestionService;
import org.example.quadlingo.service.QuizService;
import org.example.quadlingo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/learn")
public class LessonController {

    private static final Logger log = LoggerFactory.getLogger(LessonController.class);

    @Autowired
    private LessonService lessonService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private GameService gameService;

    @GetMapping
    public String learn(Model model) {
        log.info("Handling /learn request");
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }
            log.info("Authenticated user: {}", currentUser.getEmail() != null ? currentUser.getEmail() : "email not available");
            boolean isAdmin = userService.isAdmin();
            model.addAttribute("isAdmin", isAdmin);

            List<Lesson> lessons = lessonService.getAllLessons();
            if (lessons == null) {
                log.warn("lessonService.getAllLessons() returned null");
                model.addAttribute("error", "Failed to load lessons.");
                return "learn";
            }
            log.info("Loaded {} lessons", lessons.size());
            model.addAttribute("lessons", lessons);

            if (!isAdmin) {
                GameState gameState = gameService.getGameState(currentUser);
                if (gameState == null) {
                    log.warn("gameService.getGameState returned null for user: {}", currentUser.getEmail());
                    model.addAttribute("lives", 0);
                    model.addAttribute("score", 0);
                } else {
                    model.addAttribute("lives", gameState.getLives());
                    model.addAttribute("score", gameState.getScore());
                }
            }

            return "learn";
        } catch (Exception e) {
            log.error("Error processing /learn request", e);
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "learn";
        }
    }

    @PostMapping("/add")
    public String addLesson(@RequestParam String title, @RequestParam String description, @RequestParam String content, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.addLesson(title, description, content);
        } catch (Exception e) {
            log.error("Error adding lesson", e);
            model.addAttribute("error", "Failed to add lesson: " + e.getMessage());
            model.addAttribute("lessons", lessonService.getAllLessons());
            return "learn";
        }
        return "redirect:/learn";
    }

    @GetMapping("/edit")
    public String editLesson(@RequestParam Integer id, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.getLessonById(id).ifPresent(lesson -> model.addAttribute("editLesson", lesson));
            model.addAttribute("lessons", lessonService.getAllLessons());
            return "learn";
        } catch (Exception e) {
            log.error("Error editing lesson", e);
            model.addAttribute("error", "Failed to load lesson: " + e.getMessage());
            return "learn";
        }
    }

    @PostMapping("/edit")
    public String updateLesson(@RequestParam Integer id, @RequestParam String title, @RequestParam String description, @RequestParam String content, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.updateLesson(id, title, description, content);
        } catch (Exception e) {
            log.error("Error updating lesson", e);
            model.addAttribute("error", "Failed to update lesson: " + e.getMessage());
            model.addAttribute("editLesson", lessonService.getLessonById(id).orElse(null));
            model.addAttribute("lessons", lessonService.getAllLessons());
            return "learn";
        }
        return "redirect:/learn";
    }

    @PostMapping("/delete")
    public String deleteLesson(@RequestParam Integer id) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.deleteLesson(id);
        } catch (Exception e) {
            log.error("Error deleting lesson", e);
        }
        return "redirect:/learn";
    }

    @GetMapping("/quiz/manage")
    public String manageQuiz(@RequestParam Integer lessonId, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            lessonService.getLessonById(lessonId).ifPresent(lesson -> model.addAttribute("lesson", lesson));
            return "quiz_management";
        } catch (Exception e) {
            log.error("Error managing quiz", e);
            model.addAttribute("error", "Failed to load quiz management: " + e.getMessage());
            return "quiz_management";
        }
    }

    @PostMapping("/quiz/add")
    public String addQuiz(@RequestParam Integer lessonId, @RequestParam String quizTitle,
                          @RequestParam Map<String, String> allParams, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            var lessonOptional = lessonService.getLessonById(lessonId);
            if (lessonOptional.isEmpty()) {
                model.addAttribute("error", "Lesson not found");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn";
            }
            if (quizTitle == null || quizTitle.trim().isEmpty()) {
                model.addAttribute("error", "Quiz title cannot be empty");
                model.addAttribute("lesson", lessonOptional.get());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "quiz_management";
            }

            // Create the quiz
            Quiz quiz = new Quiz(quizTitle, lessonOptional.get());
            List<Question> questions = new ArrayList<>();

            // Process questions (using 0-based indexing to match form)
            int questionIndex = 0;
            while (allParams.containsKey("questions[" + questionIndex + "].text")) {
                String text = allParams.get("questions[" + questionIndex + "].text");
                String option1 = allParams.get("questions[" + questionIndex + "].option1");
                String option2 = allParams.get("questions[" + questionIndex + "].option2");
                String option3 = allParams.get("questions[" + questionIndex + "].option3");
                String option4 = allParams.get("questions[" + questionIndex + "].option4");
                String correctStr = allParams.get("questions[" + questionIndex + "].correct");

                if (text != null && option1 != null && option2 != null && option3 != null && option4 != null && correctStr != null) {
                    Integer correctOption = Integer.parseInt(correctStr);
                    if (correctOption >= 1 && correctOption <= 4) {
                        Question question = new Question(text, option1, option2, option3, option4, correctOption, quiz);
                        questions.add(question);
                    } else {
                        throw new IllegalArgumentException("Invalid correct option value for question " + (questionIndex + 1));
                    }
                } else {
                    throw new IllegalArgumentException("Missing required fields for question " + (questionIndex + 1));
                }
                questionIndex++;
            }

            // Set questions on quiz
            quiz.setQuestions(questions);

            // Save the quiz (questions will be saved due to cascade)
            quizService.addQuiz(quiz);

            return "redirect:/learn/quiz/manage?lessonId=" + lessonId;
        } catch (Exception e) {
            model.addAttribute("error", "Failed to add quiz: " + e.getMessage());
            model.addAttribute("lesson", lessonService.getLessonById(lessonId).orElse(null));
            model.addAttribute("isAdmin", userService.isAdmin());
            return "quiz_management";
        }
    }

    @GetMapping("/quiz/edit")
    public String showEditQuizForm(@RequestParam Integer id, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            var quizOptional = quizService.getQuizById(id);
            if (quizOptional.isPresent()) {
                model.addAttribute("editQuiz", quizOptional.get());
                model.addAttribute("lesson", quizOptional.get().getLesson());
                model.addAttribute("questions", questionService.getQuestionsByQuiz(quizOptional.get()));
                model.addAttribute("isAdmin", userService.isAdmin());
                return "quiz_management";
            } else {
                model.addAttribute("error", "Quiz not found");
                model.addAttribute("lessons", lessonService.getAllLessons());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "learn";
            }
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load quiz: " + e.getMessage());
            model.addAttribute("lessons", lessonService.getAllLessons());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "learn";
        }
    }

    @PostMapping("/quiz/edit")
    public String editQuiz(@RequestParam Integer id, @RequestParam String quizTitle,
                           @RequestParam Map<String, String> allParams, Model model) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            var quizOptional = quizService.getQuizById(id);
            if (quizOptional.isEmpty()) {
                model.addAttribute("error", "Quiz not found");
                model.addAttribute("editQuiz", quizService.getQuizById(id).orElse(null));
                model.addAttribute("lesson", quizService.getQuizById(id).get().getLesson());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "quiz_management";
            }
            if (quizTitle == null || quizTitle.trim().isEmpty()) {
                model.addAttribute("error", "Quiz title cannot be empty");
                model.addAttribute("editQuiz", quizService.getQuizById(id).orElse(null));
                model.addAttribute("lesson", quizService.getQuizById(id).get().getLesson());
                model.addAttribute("isAdmin", userService.isAdmin());
                return "quiz_management";
            }

            // Update quiz title
            quizService.updateQuiz(id, quizTitle);

            // Process questions
            List<Map<String, String>> questions = new ArrayList<>();
            int questionIndex = 1;
            while (allParams.containsKey("questions[" + questionIndex + "].text")) {
                Map<String, String> question = new HashMap<>();
                question.put("id", allParams.get("questions[" + questionIndex + "].id"));
                question.put("text", allParams.get("questions[" + questionIndex + "].text"));
                question.put("option1", allParams.get("questions[" + questionIndex + "].option1"));
                question.put("option2", allParams.get("questions[" + questionIndex + "].option2"));
                question.put("option3", allParams.get("questions[" + questionIndex + "].option3"));
                question.put("option4", allParams.get("questions[" + questionIndex + "].option4"));
                question.put("correct", allParams.get("questions[" + questionIndex + "].correct"));
                questions.add(question);
                questionIndex++;
            }

            // Get existing questions
            var existingQuestions = questionService.getQuestionsByQuiz(quizOptional.get());
            List<Integer> submittedQuestionIds = new ArrayList<>();
            for (Map<String, String> question : questions) {
                String text = question.get("text");
                String option1 = question.get("option1");
                String option2 = question.get("option2");
                String option3 = question.get("option3");
                String option4 = question.get("option4");
                Integer correct = Integer.parseInt(question.get("correct"));
                String idStr = question.get("id");

                if (idStr != null && !idStr.isEmpty()) {
                    // Update existing question
                    Integer questionId = Integer.parseInt(idStr);
                    submittedQuestionIds.add(questionId);
                    questionService.updateQuestion(questionId, text, option1, option2, option3, option4, correct);
                } else {
                    // Add new question
                    questionService.addQuestion(text, option1, option2, option3, option4, correct, quizOptional.get());
                }
            }

            // Delete questions that were removed
            for (var existingQuestion : existingQuestions) {
                if (!submittedQuestionIds.contains(existingQuestion.getId())) {
                    questionService.deleteQuestion(existingQuestion.getId());
                }
            }

            return "redirect:/learn/quiz/manage?lessonId=" + quizOptional.get().getLesson().getId();
        } catch (Exception e) {
            model.addAttribute("error", "Failed to edit quiz: " + e.getMessage());
            model.addAttribute("editQuiz", quizService.getQuizById(id).orElse(null));
            model.addAttribute("lesson", quizService.getQuizById(id).get().getLesson());
            model.addAttribute("isAdmin", userService.isAdmin());
            return "quiz_management";
        }
    }

    @PostMapping("/quiz/delete")
    public String deleteQuiz(@RequestParam Integer id) {
        if (!userService.isAdmin()) {
            return "redirect:/learn";
        }
        try {
            quizService.deleteQuiz(id);
        } catch (Exception e) {
            log.error("Error deleting quiz", e);
        }
        return "redirect:/learn";
    }

    @GetMapping("/quiz/take")
    public String takeQuiz(@RequestParam Integer id, Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }
            if (!gameService.canPlayGame(currentUser)) {
                model.addAttribute("error", "You have lost all lives. Please wait 5 minutes for one life to restore.");
                model.addAttribute("remainingSeconds", gameService.getRemainingSeconds(currentUser));
                return "redirect:/learn";
            }
            Quiz quiz = quizService.getQuizById(id).orElse(null);
            if (quiz == null) {
                log.warn("Quiz with id {} not found", id);
                model.addAttribute("error", "Quiz not found.");
                return "redirect:/learn";
            }
            List<Question> questions = questionService.getQuestionsByQuiz(quiz);
            if (questions == null || questions.isEmpty()) {
                log.warn("No questions found for quiz id {}", id);
                model.addAttribute("error", "No questions available for this quiz.");
                return "redirect:/learn";
            }
            // Логирование содержимого вопросов для отладки
            for (int i = 0; i < questions.size(); i++) {
                Question q = questions.get(i);
                log.debug("Question {}: text={}, options=[{}, {}, {}, {}], correct={}", i, q.getText(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4(), q.getCorrectOption());
            }
            model.addAttribute("quiz", quiz);
            model.addAttribute("questions", questions);
            model.addAttribute("currentQuestionIndex", 0);
            model.addAttribute("lives", gameService.getGameState(currentUser).getLives());
            model.addAttribute("score", gameService.getGameState(currentUser).getScore());
            model.addAttribute("total", questions.size());
            return "quiz";
        } catch (Exception e) {
            log.error("Error taking quiz for id {}: {}", id, e.getMessage(), e);
            model.addAttribute("error", "Failed to load quiz: " + e.getMessage());
            return "redirect:/learn";
        }
    }

    @PostMapping("/quiz/next")
    public String nextQuestion(@RequestParam Integer quizId, @RequestParam Integer currentQuestionIndex, @RequestParam Map<String, String> allParams, Model model) {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                log.info("No authenticated user found, redirecting to login");
                return "redirect:/login";
            }

            Quiz quiz = quizService.getQuizById(quizId).orElse(null);
            if (quiz == null) {
                log.warn("Quiz with id {} not found", quizId);
                model.addAttribute("error", "Quiz not found.");
                return "redirect:/learn";
            }

            List<Question> questions = questionService.getQuestionsByQuiz(quiz);
            if (questions == null || questions.isEmpty()) {
                log.warn("No questions found for quiz id {}", quizId);
                model.addAttribute("error", "No questions available for this quiz.");
                return "redirect:/learn";
            }

            if (currentQuestionIndex < 0 || currentQuestionIndex >= questions.size()) {
                log.warn("Invalid currentQuestionIndex: {}, redirecting to learn", currentQuestionIndex);
                model.addAttribute("error", "Invalid quiz state.");
                return "redirect:/learn";
            }

            Question currentQuestion = questions.get(currentQuestionIndex);
            String answerKey = "answer_" + currentQuestion.getId();
            String answer = allParams.get(answerKey);

            boolean isCorrect = false;
            if (answer != null && !answer.isEmpty()) {
                try {
                    int selectedOption = Integer.parseInt(answer);
                    isCorrect = (selectedOption == currentQuestion.getCorrectOption() && selectedOption >= 1 && selectedOption <= 4);
                } catch (NumberFormatException e) {
                    log.warn("Invalid answer format for question id {}: {}", currentQuestion.getId(), answer);
                    isCorrect = false;
                }
            } else {
                log.warn("No answer selected for question id {}", currentQuestion.getId());
                isCorrect = false;
            }

            gameService.checkQuizAnswer(currentUser, isCorrect);
            GameState gameState = gameService.getGameState(currentUser);
            if (gameState == null) {
                log.error("Game state is null for user {}", currentUser.getEmail());
                model.addAttribute("error", "Failed to retrieve game state.");
                return "redirect:/learn";
            }

            if (!isCorrect && gameState.getLives() <= 0) {
                log.info("User {} lost all lives during quiz, ending quiz", currentUser.getEmail());
                model.addAttribute("score", gameState.getScore());
                model.addAttribute("lives", 0);
                model.addAttribute("total", questions.size());
                model.addAttribute("currentQuestionIndex", null);
                model.addAttribute("error", "You have lost all lives. Please wait 5 minutes for one life to restore.");
                model.addAttribute("remainingSeconds", gameService.getRemainingSeconds(currentUser));
                model.addAttribute("quiz", quiz);
                return "quiz";
            }

            if (currentQuestionIndex + 1 < questions.size()) {
                log.debug("Moving to next question: {}/{}", currentQuestionIndex + 1, questions.size());
                model.addAttribute("quiz", quiz);
                model.addAttribute("questions", questions);
                model.addAttribute("currentQuestionIndex", currentQuestionIndex + 1);
                model.addAttribute("lives", gameState.getLives());
                model.addAttribute("score", gameState.getScore());
                model.addAttribute("total", questions.size());
                return "quiz";
            } else {
                log.info("User {} completed quiz with score: {}, lives: {}", currentUser.getEmail(), gameState.getScore(), gameState.getLives());
                model.addAttribute("quiz", quiz);
                model.addAttribute("score", gameState.getScore());
                model.addAttribute("lives", gameState.getLives());
                model.addAttribute("total", questions.size());
                model.addAttribute("currentQuestionIndex", null);
                model.addAttribute("quizCompleted", true);

                // Увеличиваем счетчик завершенных квизов
                gameService.incrementQuizzesCompleted(currentUser);

                return "quiz";
            }
        } catch (Exception e) {
            log.error("Error processing next question for quizId {}: {}", quizId, e.getMessage(), e);
            model.addAttribute("error", "An error occurred while processing the quiz: " + e.getMessage());
            return "quiz";
        }
    }
}