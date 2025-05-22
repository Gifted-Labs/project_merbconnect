package com.merbsconnect.academics.domain;

import com.merbsconnect.enums.QuizType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DiscriminatorValue("QUIZ")
public class Quiz extends Resource{

    private int numberOfQuestions;

    private String difficultyLevel;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "quiz_id")
    private List<Question> questions;

    private int yearGiven;

    @Enumerated(EnumType.STRING)
    private QuizType quizType;
}
