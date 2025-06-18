package com.merbsconnect.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Sponsors {

    @Id
    @GeneratedValue(strategy= jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String sponsorName;

    private String sponsorImageUrl;

    @ManyToOne
    @JoinColumn(name="event_id")
    private Event event;
}
