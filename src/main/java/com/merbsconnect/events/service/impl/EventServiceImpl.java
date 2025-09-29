package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.Registration;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.EventService;
import com.merbsconnect.exception.BusinessException;
import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import com.merbsconnect.sms.service.SmsService;
import com.merbsconnect.util.mapper.EventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final SmsService smsService;

    @Transactional
    @Override
    @CachePut(value = "events", key = "#eventRequest.title + '-' + #eventRequest.date")
    public EventResponse createEvent(CreateEventRequest eventRequest) {
        if(eventRequest.getDate().isBefore(java.time.LocalDate.now())){
            throw new BusinessException("Event date cannot be in the past");
        }

        if(eventRepository.existsByTitleAndDate(eventRequest.getTitle(), eventRequest.getDate())){
            throw new BusinessException("Event Already Exist");
        }
        Event event = EventMapper.mapToEvent(eventRequest);
        return EventMapper.mapToEventResponse(eventRepository.save(event));
    }

    @Override
    @CachePut(value = "events", key = "#eventId")
    public EventResponse updateEvent(UpdateEventRequest eventRequest, Long eventId) {
        Event existingEvent = getEventByIdInternal(eventId);

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
    @CacheEvict(value = "events", key = "#eventId")
    public MessageResponse deleteEvent(Long eventId) {
        Event event = getEventByIdInternal(eventId);

        eventRepository.delete(event);

        return MessageResponse.builder()
                .message("Event successfully deleted")
                .build();
    }

    @Override
    @Cacheable(value = "events")
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);

        return events.map(EventMapper::mapToEventResponse);
    }

    @Override
    @Cacheable(value = "events", key = "#eventId")
    public Optional<EventResponse> getEventById(Long eventId) {
        Event event = getEventByIdInternal(eventId);

        return Optional.of(EventMapper.mapToEventResponse(event));
    }

    @Override
    @Cacheable(value = "events", key = "#year")
    public Optional<EventResponse> getEventByYear(Long year) {

        if(!eventRepository.existsEventByYear(year)){
            throw new BusinessException("No event exist for the given year");
        }
        return eventRepository.findEventByYear(year)
                .map(EventMapper::mapToEventResponse);
    }

    @Override
    @Transactional
    @CachePut(value = "events", key = "#eventId")
    public MessageResponse addSpeakerToEvent(Speaker speaker, Long eventId) {
        Event event = getEventByIdInternal(eventId);
        if(event.getSpeakers().contains(speaker)){
            throw new BusinessException("Speaker is already added");
        }
        event.getSpeakers().add(speaker);
        eventRepository.save(event);
        return MessageResponse.builder()
                .message("Speaker Added Successfully")
                .build();
    }

    @Override
    @CacheEvict(value = "events", key = "#eventId")
    public MessageResponse removeSpeakerFromEvent(Long eventId, String speakerName) {
        Event event = getEventByIdInternal(eventId);

        Speaker speakerToRemove = event.getSpeakers().stream()
                .filter(speaker -> speaker.getName().toLowerCase().contains(speakerName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Speaker not found with name: " + speakerName));


        event.getSpeakers().remove(speakerToRemove);
        eventRepository.save(event);
        return MessageResponse.builder()
                .message("Speaker Removed Successfully")
                .build();
    }

    @Override
    @Cacheable(value = "events", key = "'upcoming'")
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        return eventRepository.findEventByDateAfter(LocalDate.now(),pageable )
                .map(EventMapper::mapToEventResponse);
    }

    @Override
    @Cacheable(value = "events", key = "'past'")
    public Page<EventResponse> getPastEvents(Pageable pageable) {

        return eventRepository.findEventByDateBefore(LocalDate.now(), pageable)
                .map(EventMapper::mapToEventResponse);
    }

    @Override
    @CachePut(value = "events", key = "#eventId")
    public MessageResponse updateEventSpeaker(Speaker updatedSpeaker, Long eventId) {
        Event event = getEventByIdInternal(eventId);
        if(event.getSpeakers().contains(updatedSpeaker)){
            throw new BusinessException("Speaker is already added");
        }

        Speaker existingSpeaker = event.getSpeakers().stream()
                .filter(s -> s.getName().toLowerCase().contains(updatedSpeaker.getName().toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Speaker not found with name: " + updatedSpeaker.getName()));

        existingSpeaker.setName(updatedSpeaker.getName());
        existingSpeaker.setBio(updatedSpeaker.getBio());
        existingSpeaker.setImageUrl(updatedSpeaker.getImageUrl());

        eventRepository.save(event);

        return MessageResponse
                .builder()
                .message("Speaker updated successfully")
                .build();
    }

    @Transactional
    @CachePut(value = "registrations", key = "#eventId")
    public MessageResponse registerForEvent(Long eventId, EventRegistrationDto registrationDto) {
        Event event = getEventByIdInternal(eventId);

        if (event.getDate().isBefore(LocalDate.now())) {
            throw new BusinessException("You can only register for an upcoming event.");
        }

        log.info("Event Date: {}", event.getDate());
        if (event.getRegistrations().stream().anyMatch(registration ->
                registration.getEmail().equalsIgnoreCase(registrationDto.getEmail()))) {
            throw new BusinessException("You have already registered for this event.");
        }

        Registration registration = EventMapper.mapToRegistration(registrationDto);
        event.getRegistrations().add(registration);
        eventRepository.save(event);

        // Send confirmation SMS
        sendRegistrationConfirmationSms(registration, event);

        return MessageResponse.builder()
                .message("Congratulations! You have successfully registered for the event")
                .build();
    }

    @Transactional
    @Cacheable(value = "registrations", key = "#eventId")
    public Page<Registration> getEventRegistrations(Long eventId, Pageable pageable) {
        Event event = getEventByIdInternal(eventId);

        List<Registration> registrationsList = new ArrayList<>(event.getRegistrations());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), registrationsList.size());


        return new PageImpl<>(registrationsList.subList(start, end), pageable, registrationsList.size());
    }

    @Transactional(readOnly = true)
    public void writeRegistrationsToCsv(Long eventId, OutputStream outputStream) throws IOException {

        Event event = getEventByIdInternal(eventId);

        Set<Registration> registrations = event.getRegistrations();

        outputStream.write("Name,Email,Phone,Notes\n".getBytes());

        for(Registration registration : registrations){
            String line = String.format("%s, %s, %s, %s%n",
                    registration.getName(),
                    registration.getEmail(),
                    registration.getPhone(),
                    registration.getNote());
            outputStream.write(line.getBytes());
        }

    }


    private Event getEventByIdInternal(Long eventId){
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with id: " + eventId));
    }


    private void sendRegistrationConfirmationSms(Registration registration, Event event) {
        try {
            // Construct the message with placeholders replaced by actual values
            String message = String.format(
                    "Dear %s,\n" +
                            "Thank you for registering for %s!\n" +
                            "Event Details:\n" +
                            "Date: %s\n" +
                            "Time: %s\n" +
                            "Venue: %s\n" +
                            "Your confirmation number is: %s.\n" +
                            "We look forward to seeing you there!",
                    registration.getName(),
                    event.getTitle(),
                    event.getDate(),
                    event.getTime(),
                    event.getLocation(),
                    registration.getEmail() // Using email as confirmation number
            );

            // Prepare the SMS request
            BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                    .recipients(List.of(registration.getPhone()))
                    .sender("MerbConnect") // Replace with your sender ID
                    .message(message)
                    .isScheduled(false)
                    .scheduleDate("")
                    .build();

            // Send the SMS synchronously
            BulkSmsResponse smsResponse = smsService.sendBulkSms(smsRequest);

            // Log the result
            if (smsResponse.isSuccessful()) {
                log.info("Registration confirmation SMS sent to {}", registration.getPhone());
            } else {
                log.error("Failed to send registration confirmation SMS: {}", smsResponse.getMessage());
            }
        } catch (Exception e) {
            log.error("Failed to send registration confirmation SMS: {}", e.getMessage());
        }
    }
}
