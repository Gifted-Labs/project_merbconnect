package com.merbsconnect.academics.domain;

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
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String questionText;

    @ElementCollection
    private List<String> possibleAnswers;

    private String correctAnswer;

    @ElementCollection
    private List<String> explanationSteps;

    @ManyToOne
    @JoinColumn(name="resource_id")
    private Resource referencedResource;
    
    // Add new fields for the question bank system
    
    private String difficultyLevel;
    
    @ElementCollection
    private List<String> tags;
    
    // Metadata fields
    private Integer usageCount;
    private Double successRate;
}
