package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.SendBulkSmsToRegistrationsRequest;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;



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
        if (eventRequest.getDate().isBefore(java.time.LocalDate.now())) {
            throw new BusinessException("Event date cannot be in the past");
        }

        if (eventRepository.existsByTitleAndDate(eventRequest.getTitle(), eventRequest.getDate())) {
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
                !existingEvent.getDate().equals(eventRequest.getDate()))
                && eventRepository.existsByTitleAndDate(eventRequest.getTitle(), eventRequest.getDate())) {
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
    @CacheEvict(value = { "events", "registrations" }, key = "#eventId")
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

        if (!eventRepository.existsEventByYear(year)) {
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
        if (event.getSpeakers().contains(speaker)) {
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
        return eventRepository.findEventByDateAfter(LocalDate.now(), pageable)
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
        if (event.getSpeakers().contains(updatedSpeaker)) {
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
    @CacheEvict(value = "registrations", key = "#eventId")
    public MessageResponse registerForEvent(Long eventId, EventRegistrationDto registrationDto) {
        Event event = getEventByIdInternal(eventId);

        if (event.getDate().isBefore(LocalDate.now())) {
            throw new BusinessException("You can only register for an upcoming event.");
        }

        log.info("Event Date: {}", event.getDate());
        if (event.getRegistrations().stream()
                .anyMatch(registration -> registration.getEmail().equalsIgnoreCase(registrationDto.getEmail()))) {
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

        return new PageImpl<>(
                registrationsList.subList(start, end),
                pageable,
                registrationsList.size());
    }

    @Transactional(readOnly = true)
    public void writeRegistrationsToCsv(Long eventId, OutputStream outputStream) throws IOException {

        Event event = getEventByIdInternal(eventId);

        Set<Registration> registrations = event.getRegistrations();

        outputStream.write("Name,Email,Phone,Notes\n".getBytes());

        for (Registration registration : registrations) {
            String line = String.format("%s, %s, %s, %s%n",
                    registration.getName(),
                    registration.getEmail(),
                    registration.getPhone(),
                    registration.getNote());
            outputStream.write(line.getBytes());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BulkSmsResponse sendBulkSmsToSelectedRegistrations(SendBulkSmsToRegistrationsRequest request) {
        // Validate event exists
        Event event = getEventByIdInternal(request.getEventId());

        // Filter registrations by selected emails
        List<String> phoneNumbers = event.getRegistrations().stream()
                .filter(registration -> request.getSelectedEmails().contains(registration.getEmail()))
                .map(Registration::getPhone)
                .toList();

        if (phoneNumbers.isEmpty()) {
            throw new BusinessException("No valid registrations found for the selected emails");
        }

        // Create and send bulk SMS request
        BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                .recipients(phoneNumbers)
                .message(request.getMessage())
                .isScheduled(false)
                .scheduleDate("")
                .build();

        log.info("Sending bulk SMS to {} registrations for event ID: {}", phoneNumbers.size(), request.getEventId());
        return smsService.sendBulkSms(smsRequest);
    }

    private Event getEventByIdInternal(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with id: " + eventId));
    }

    @Async
    protected void sendRegistrationConfirmationSms(Registration registration, Event event) {
        try {
            // Construct the message with placeholders replaced by actual values
            String message = String.format(
                    """
                            Dear %s,
                            Thank you for registering for %s!
                            Event Details:
                            Date: %s
                            Time: %s
                            Venue: %s
                            Your confirmation number is: %s.
                            We look forward to seeing you there!""",
                    registration.getName(),
                    event.getTitle(),
                    event.getDate(),
                    event.getTime(),
                    event.getLocation(),
                    registration.getEmail());

            // Prepare the SMS request
            BulkSmsRequest smsRequest = BulkSmsRequest.builder()
                    .recipients(List.of(registration.getPhone()))
                    .sender("MerbConnect")
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

    // ==================== ADMIN DASHBOARD ENDPOINTS ====================

    @Override
    @Transactional(readOnly = true)
    public com.merbsconnect.events.dto.response.EventStatsResponse getEventStats() {
        long totalEvents = eventRepository.count();
        long upcomingEvents = eventRepository.findEventByDateAfter(LocalDate.now(), Pageable.unpaged())
                .getTotalElements();
        long pastEvents = eventRepository.findEventByDateBefore(LocalDate.now(), Pageable.unpaged()).getTotalElements();

        // Calculate total registrations across all events
        List<Event> allEvents = eventRepository.findAll();
        long totalRegistrations = allEvents.stream()
                .mapToLong(event -> event.getRegistrations().size())
                .sum();

        // Calculate average registrations per event
        double averageRegistrations = totalEvents > 0 ? (double) totalRegistrations / totalEvents : 0.0;

        return com.merbsconnect.events.dto.response.EventStatsResponse.builder()
                .totalEvents(totalEvents)
                .upcomingEvents(upcomingEvents)
                .pastEvents(pastEvents)
                .totalRegistrations(totalRegistrations)
                .averageRegistrationsPerEvent(Math.round(averageRegistrations * 100.0) / 100.0)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.merbsconnect.events.dto.response.EventAnalyticsResponse getEventAnalytics(Long eventId) {
        Event event = getEventByIdInternal(eventId);

        // Determine event status
        String eventStatus;
        LocalDate today = LocalDate.now();
        if (event.getDate().isAfter(today)) {
            eventStatus = "UPCOMING";
        } else if (event.getDate().isBefore(today)) {
            eventStatus = "PAST";
        } else {
            eventStatus = "ONGOING";
        }

        return com.merbsconnect.events.dto.response.EventAnalyticsResponse.builder()
                .eventId(event.getId())
                .eventTitle(event.getTitle())
                .eventDate(event.getDate())
                .totalRegistrations((long) event.getRegistrations().size())
                .speakerCount(event.getSpeakers().size())
                .eventStatus(eventStatus)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = { "events", "registrations" }, key = "#eventId")
    public MessageResponse deleteRegistration(Long eventId, String email) {
        Event event = getEventByIdInternal(eventId);

        Registration registrationToRemove = event.getRegistrations().stream()
                .filter(reg -> reg.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Registration not found with email: " + email));

        event.getRegistrations().remove(registrationToRemove);
        eventRepository.save(event);

        log.info("Deleted registration with email {} from event ID {}", email, eventId);

        return MessageResponse.builder()
                .message("Registration deleted successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.merbsconnect.events.dto.response.RegistrationStatsResponse getRegistrationStats(
            LocalDate startDate, LocalDate endDate) {

        List<Event> events;

        // Filter events by date range if provided
        if (startDate != null && endDate != null) {
            events = eventRepository.findAll().stream()
                    .filter(event -> !event.getDate().isBefore(startDate) && !event.getDate().isAfter(endDate))
                    .toList();
        } else {
            events = eventRepository.findAll();
        }

        // Calculate total registrations
        long totalRegistrations = events.stream()
                .mapToLong(event -> event.getRegistrations().size())
                .sum();

        // Build registrations by event
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.EventRegistrationCount> registrationsByEvent = events
                .stream()
                .map(event -> com.merbsconnect.events.dto.response.RegistrationStatsResponse.EventRegistrationCount
                        .builder()
                        .eventName(event.getTitle())
                        .count((long) event.getRegistrations().size())
                        .build())
                .toList();

        // Get top 5 events by registrations
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto> topEvents = events.stream()
                .sorted((e1, e2) -> Integer.compare(e2.getRegistrations().size(), e1.getRegistrations().size()))
                .limit(5)
                .map(event -> com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto.builder()
                        .eventTitle(event.getTitle())
                        .registrationCount((long) event.getRegistrations().size())
                        .build())
                .toList();

        return com.merbsconnect.events.dto.response.RegistrationStatsResponse.builder()
                .totalRegistrations(totalRegistrations)
                .registrationsByEvent(registrationsByEvent)
                .topEventsByRegistrations(topEvents)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Speaker> getEventSpeakers(Long eventId) {
        Event event = getEventByIdInternal(eventId);
        return new ArrayList<>(event.getSpeakers());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", unless = "#result == null")
    public com.merbsconnect.events.dto.response.DashboardResponse getDashboardData() {
        // Get overall stats
        com.merbsconnect.events.dto.response.EventStatsResponse stats = getEventStats();

        // Get recent 5 events (sorted by creation date)
        List<EventResponse> recentEvents = eventRepository.findAll(
                org.springframework.data.domain.PageRequest.of(0, 5,
                        org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                                "createdAt")))
                .stream()
                .map(EventMapper::mapToEventResponse)
                .toList();

        // Get top 5 events by registrations
        List<Event> allEvents = eventRepository.findAll();
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto> topEvents = allEvents.stream()
                .sorted((e1, e2) -> Integer.compare(e2.getRegistrations().size(), e1.getRegistrations().size()))
                .limit(5)
                .map(event -> com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto.builder()
                        .eventTitle(event.getTitle())
                        .registrationCount((long) event.getRegistrations().size())
                        .build())
                .toList();

        // Determine system status (simple health check)
        String systemStatus = "HEALTHY";

        return com.merbsconnect.events.dto.response.DashboardResponse.builder()
                .overallStats(stats)
                .recentEvents(recentEvents)
                .topEvents(topEvents)
                .systemStatus(systemStatus)
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = { "events", "registrations" }, key = "#eventId")
    public MessageResponse deleteMultipleRegistrations(Long eventId, List<String> emails) {
        Event event = getEventByIdInternal(eventId);

        int deletedCount = 0;
        for (String email : emails) {
            Optional<Registration> registration = event.getRegistrations().stream()
                    .filter(reg -> reg.getEmail().equalsIgnoreCase(email))
                    .findFirst();

            if (registration.isPresent()) {
                event.getRegistrations().remove(registration.get());
                deletedCount++;
            }
        }

        eventRepository.save(event);

        log.info("Deleted {} registrations from event ID {}", deletedCount, eventId);

        return MessageResponse.builder()
                .message(String.format("Successfully deleted %d registration(s)", deletedCount))
                .build();
    }
}
