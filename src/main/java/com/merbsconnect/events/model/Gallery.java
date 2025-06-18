package com.merbsconnect.events.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Gallery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private List<String> imageUrl;
    private String coverImageUrl;
    private LocalDateTime createdAt;

}
