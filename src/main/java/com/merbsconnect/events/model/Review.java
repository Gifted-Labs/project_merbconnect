package com.merbsconnect.events.model;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.enums.AcademicLevel;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "event_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "event")
@EqualsAndHashCode(exclude = "event")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private com.merbsconnect.authentication.domain.User user;

    // Guest fields
    private String guestName;

    @Enumerated(EnumType.STRING)
    private AcademicLevel guestAcademicLevel;

    private String guestProgram;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
