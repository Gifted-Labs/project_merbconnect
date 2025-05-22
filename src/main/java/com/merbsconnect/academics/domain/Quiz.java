package com.merbsconnect.academics.domain;

import com.merbsconnect.enums.QuizType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("QUIZ")
public class Quiz extends Resource{

    private int numberOfQuestions;

    @Column(nullable = false)
    private String difficultyLevel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    @Builder.Default
    private List<Question> questions = new java.util.ArrayList<>();

    private int yearGiven;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType quizType;


}
