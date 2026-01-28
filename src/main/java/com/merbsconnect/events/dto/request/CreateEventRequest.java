package com.merbsconnect.events.dto.request;

import com.merbsconnect.events.model.Contact;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.model.Sponsors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    private String title;
    private String description;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String imageUrl;
    private Set<Speaker> speakers;
    private Set<Contact> contacts;
    private Set<Sponsors> sponsors;

    /**
     * Theme of the event (e.g., "Innovation & Technology", "Faith & Leadership")
     */
    private String theme;
}
