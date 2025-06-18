package com.merbsconnect.events.service;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Speaker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EventService {

    EventResponse createEvent(CreateEventRequest eventRequest);

    EventResponse updateEvent(UpdateEventRequest eventRequest, Long eventId);

    MessageResponse deleteEvent(Long eventId);

    Page<EventResponse> getAllEvents(Pageable pageable);

    Optional<EventResponse> getEventById(Long eventId);

    Optional<EventResponse> getEventByYear(Long year);

    MessageResponse addSpeakerToEvent(Speaker speaker, Long eventId);

    MessageResponse removeSpeakerFromEvent(Long eventId,Long speakerId);

}
