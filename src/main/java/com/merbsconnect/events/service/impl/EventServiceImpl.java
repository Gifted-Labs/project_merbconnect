package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.EventService;
import com.merbsconnect.exception.BusinessException;
import com.merbsconnect.util.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventResponse createEvent(CreateEventRequest eventRequest) {
        if (eventRepository.existsEventByYear(eventRequest.getDate().getYear())) {
            throw new BusinessException("Event Already Exist");
        }
        if(eventRepository.existsByTitleAndDate(eventRequest.getTitle(), eventRequest.getDate())){
            throw new BusinessException("Event Already Exist");
        }
        Event event = EventMapper.mapToEvent(eventRequest);
        return EventMapper.mapToEventResponse(eventRepository.save(event));
    }

    @Override
    public EventResponse updateEvent(UpdateEventRequest eventRequest, Long eventId) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with id: " + eventId));

        if ((!existingEvent.getTitle().equals(eventRequest.getTitle()) ||
                !existingEvent.getDate().equals(eventRequest.getDate())) && eventRepository.existsByTitleAndDate(eventRequest.getTitle(), eventRequest.getDate())) {
            throw new BusinessException("Event with this title and date already exists");
        }
        existingEvent.setTitle(eventRequest.getTitle());
        existingEvent.setDescription(eventRequest.getDescription());
        existingEvent.setLocation(eventRequest.getLocation());
        existingEvent.setDate(eventRequest.getDate());
        existingEvent.setTime(eventRequest.getTime());
        existingEvent.setImageUrl(eventRequest.getImageUrl());

        Event updatedEvent = eventRepository.save(existingEvent);
        return EventMapper.mapToEventResponse(updatedEvent);
    }


    @Override
    public MessageResponse deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with id: " + eventId));

        eventRepository.delete(event);

        return MessageResponse.builder()
                .message("Event successfully deleted")
                .build();
    }

    @Override
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);

        return events.map(EventMapper::mapToEventResponse);
    }

    @Override
    public Optional<EventResponse> getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .map(EventMapper::mapToEventResponse);
    }

    @Override
    public Optional<EventResponse> getEventByYear(Long year) {
        return Optional.empty();
    }


    @Override
    public MessageResponse addSpeakerToEvent(Speaker speaker, Long eventId) {
        return null;
    }

    @Override
    public MessageResponse removeSpeakerFromEvent(Long eventId, Long speakerId) {
        return null;
    }


}
