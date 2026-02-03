package com.merbsconnect.events.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private List<String> imageUrl;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
