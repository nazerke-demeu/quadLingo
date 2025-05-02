package org.example.quadlingo;

import jakarta.persistence.*;

@Entity
@Table(name = "words")
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String kazakh;

    @Column(nullable = false)
    private String english;

    @Column(nullable = false)
    private String difficulty; // "EASY", "MEDIUM", "HARD"

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKazakh() {
        return kazakh;
    }

    public void setKazakh(String kazakh) {
        this.kazakh = kazakh;
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}