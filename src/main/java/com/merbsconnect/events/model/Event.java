package com.merbsconnect.events.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Event {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private LocalDate date;

    private LocalTime time;

    @OneToOne(mappedBy="event", cascade=CascadeType.ALL)
    private Gallery gallery;

    @OneToMany(mappedBy="event", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Sponsors> sponsors;

    private String imageUrl;

    private String createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy="event", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Speaker> speakers;

    @OneToMany(mappedBy="event", cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Testimonials> testimonials;

    private String videoUrl;


    @PostConstruct
    public void init(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = (LocalDateTime.now());
    }
}
