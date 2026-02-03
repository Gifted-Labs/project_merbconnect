package com.merbsconnect.events.dto.response;

import com.merbsconnect.events.model.Contact;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.model.Sponsors;
import com.merbsconnect.events.model.Testimonials;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDate date;
    private LocalTime time;
    private String imageUrl;
    private Set<Speaker> speakers;
    private Set<Testimonials> testimonials;
    private Set<Sponsors> sponsors;
    private Set<Contact> contacts;
    private String videoUrl;

    /**
     * Theme of the event
     */
    private String theme;

    /**
     * Enhanced speakers with S3 images (v2)
     */
    private Set<EventSpeakerResponse> speakersV2;

    /**
     * Event itinerary/program lineup
     */
    private Set<EventItineraryItemResponse> itinerary;
}
