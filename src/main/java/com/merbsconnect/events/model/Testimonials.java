package com.merbsconnect.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Testimonials {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String fullName;
    
    private String level;
    
    @Column(length = 1000)
    private String testimonial;
    
    private String imageUrl;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;
}
