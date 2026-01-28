package com.merbsconnect.events.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventRequest {

    private String title;
    private String description;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String imageUrl;
    private String videoUrl;
    private String theme;

}
