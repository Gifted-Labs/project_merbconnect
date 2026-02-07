package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.enums.MediaType;
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
import com.merbsconnect.storage.StorageService;
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
import org.springframework.web.multipart.MultipartFile;

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
    private final com.merbsconnect.events.repository.EventRegistrationRepository eventRegistrationRepository;
    private final SmsService smsService;
    private final StorageService storageService;

    @Transactional
    @Override
    @CacheEvict(value = "events", allEntries = true)
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
    @CacheEvict(value = "events", allEntries = true)
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
        existingEvent.setTheme(eventRequest.getTheme());

        Event updatedEvent = eventRepository.save(existingEvent);
        return EventMapper.mapToEventResponse(updatedEvent);
    }

    @Override
    @CacheEvict(value = { "events", "registrations" }, allEntries = true)
    public MessageResponse deleteEvent(Long eventId) {
        Event event = getEventByIdInternal(eventId);

        // Explicitly delete usage of ElementCollections to prevent FK violation via
        // Native SQL
        eventRepository.deleteEventSponsors(eventId);
        eventRepository.deleteEventSpeakers(eventId);
        eventRepository.deleteEventContacts(eventId);
        eventRepository.deleteEventRegistrations(eventId);

        eventRepository.delete(event);

        return MessageResponse.builder()
                .message("Event successfully deleted")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events")
    public Page<EventResponse> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);

        return events.map(event -> enrichWithPresignedUrls(EventMapper.mapToEventSummary(event)));
    }

    /**
     * Enriches an EventResponse with presigned URLs for S3 images.
     * Converts stored S3 URLs to accessible presigned URLs.
     */
    private EventResponse enrichWithPresignedUrls(EventResponse response) {
        // Generate presigned URL for event banner image
        if (response.getImageUrl() != null && !response.getImageUrl().isEmpty()) {
            String presignedUrl = storageService.generatePresignedUrl(response.getImageUrl());
            response.setImageUrl(presignedUrl);
        }

        // Generate presigned URLs for speakersV2 images
        if (response.getSpeakersV2() != null) {
            response.getSpeakersV2().forEach(speaker -> {
                if (speaker.getImageUrl() != null && !speaker.getImageUrl().isEmpty()) {
                    String presignedUrl = storageService.generatePresignedUrl(speaker.getImageUrl());
                    speaker.setImageUrl(presignedUrl);
                }
            });
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "#eventId")
    public Optional<EventResponse> getEventById(Long eventId) {
        Event event = eventRepository.findWithDetailsById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with id: " + eventId));

        return Optional.of(enrichWithPresignedUrls(EventMapper.mapToEventResponse(event)));
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
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'upcoming'")
    public Page<EventResponse> getUpcomingEvents(Pageable pageable) {
        return eventRepository.findEventByDateAfter(LocalDate.now(), pageable)
                .map(event -> enrichWithPresignedUrls(EventMapper.mapToEventSummary(event)));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "events", key = "'past'")
    public Page<EventResponse> getPastEvents(Pageable pageable) {
        return eventRepository.findEventByDateBefore(LocalDate.now(), pageable)
                .map(event -> enrichWithPresignedUrls(EventMapper.mapToEventSummary(event)));
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

    @Transactional(readOnly = true)
    public Page<EventRegistrationDto> getEventRegistrations(Long eventId, Pageable pageable) {
        // Fetch V2 registrations from the new repository
        Page<com.merbsconnect.events.model.EventRegistration> v2Registrations = eventRegistrationRepository
                .findByEventId(eventId, pageable);

        // Map V2 entity to DTO including ID
        return v2Registrations.map(v2Reg -> EventRegistrationDto.builder()
                .id(v2Reg.getId())
                .name(v2Reg.getName())
                .email(v2Reg.getEmail())
                .phone(v2Reg.getPhone())
                .note(v2Reg.getNote())
                .program(v2Reg.getProgram())
                .academicLevel(v2Reg.getAcademicLevel())
                .university(v2Reg.getUniversity())
                .referralSource(v2Reg.getReferralSource())
                .referralSourceOther(v2Reg.getReferralSourceOther())
                .needsShirt(v2Reg.isNeedsShirt())
                .shirtSize(v2Reg.getShirtSize())
                .build());
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

        // Filter registrations by selected emails calling V2 repository
        List<com.merbsconnect.events.model.EventRegistration> registrations = eventRegistrationRepository
                .findByEventIdAndEmailIn(request.getEventId(), request.getSelectedEmails());

        // Check if message contains placeholders
        boolean hasPlaceholders = request.getMessage().contains("[fname]") || request.getMessage().contains("[name]");

        if (hasPlaceholders) {
            // Personalized sending (One request per user, Parallel execution)
            List<java.util.concurrent.CompletableFuture<BulkSmsResponse>> futures = registrations.stream()
                    .map(registration -> {
                        // Personalize message
                        String personalizedMessage = request.getMessage();
                        if (personalizedMessage.contains("[fname]")) {
                            String firstName = registration.getName().split(" ")[0]; // Simple split by space
                            personalizedMessage = personalizedMessage.replace("[fname]", firstName);
                        }
                        if (personalizedMessage.contains("[name]")) {
                            personalizedMessage = personalizedMessage.replace("[name]", registration.getName());
                        }

                        // Create request for single recipient
                        BulkSmsRequest individualRequest = BulkSmsRequest.builder()
                                .recipients(List.of(registration.getPhone()))
                                .message(personalizedMessage)
                                .isScheduled(false) // TODO: Support scheduling for bulk
                                .build();

                        return smsService.sendBulkSmsAsync(individualRequest);
                    })
                    .toList();

            // Wait for all to complete
            java.util.concurrent.CompletableFuture.allOf(futures.toArray(new java.util.concurrent.CompletableFuture[0]))
                    .join();

            // Aggregate results (Simple success count)
            long successCount = futures.stream()
                    .filter(f -> {
                        try {
                            return f.get().isSuccessful();
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .count();

            return BulkSmsResponse.builder()
                    .code("200")
                    .message(String.format("Bulk SMS processing completed. Successfully sent: %d/%d", successCount,
                            futures.size()))
                    .build(); // Using generic response structure

        } else {
            // Standard Bulk sending (One request, Many recipients) - More efficient for
            // static messages
            List<String> phoneNumbers = registrations.stream()
                    .map(com.merbsconnect.events.model.EventRegistration::getPhone)
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

            log.info("Sending bulk SMS to {} registrations for event ID: {}", phoneNumbers.size(),
                    request.getEventId());
            return smsService.sendBulkSms(smsRequest);
        }
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
        long upcomingEvents = eventRepository.countByDateAfter(LocalDate.now());
        long pastEvents = eventRepository.countByDateBefore(LocalDate.now());

        // Calculate total registrations across all events (V1 + V2)
        long v1Count = eventRegistrationRepository.countAllV1Registrations();
        long v2Count = eventRegistrationRepository.count();
        long totalRegistrations = v1Count + v2Count;

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
                .totalRegistrations((long) ((event.getRegistrations() != null ? event.getRegistrations().size() : 0) +
                        (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0)))
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
            events = eventRepository.findByDateBetween(startDate, endDate);
        } else {
            // Still loading all events for summary, but now with LAZY loading it's much faster/leaner
            events = eventRepository.findAll();
        }

        // Calculate total registrations (V1 + V2)
        long totalRegistrations = events.stream()
                .mapToLong(event -> {
                    long v1 = (event.getRegistrations() != null) ? event.getRegistrations().size() : 0;
                    long v2 = (event.getRegistrationsV2() != null) ? event.getRegistrationsV2().size() : 0;
                    return v1 + v2;
                })
                .sum();

        // Build registrations by event (V1 + V2)
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.EventRegistrationCount> registrationsByEvent = events
                .stream()
                .map(event -> {
                    long count = ((event.getRegistrations() != null ? event.getRegistrations().size() : 0) +
                            (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0));
                    return com.merbsconnect.events.dto.response.RegistrationStatsResponse.EventRegistrationCount
                            .builder()
                            .eventName(event.getTitle())
                            .count(count)
                            .build();
                })
                .toList();

        // Get top 5 events across all time for comparison
        List<Long> topEventIds = eventRepository.findTopEventIdsByRegistrationCount(5);
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto> topEvents = topEventIds.stream()
                .map(id -> {
                    Event event = eventRepository.findById(id).orElse(null);
                    if (event == null) return null;
                    long count = ((event.getRegistrations() != null ? event.getRegistrations().size() : 0) +
                            (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0));
                    return com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto.builder()
                            .eventTitle(event.getTitle())
                            .registrationCount(count)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
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
                .map(EventMapper::mapToEventSummary)
                .toList();

        // Get top 5 events by registrations (SQL optimized)
        List<Long> topEventIds = eventRepository.findTopEventIdsByRegistrationCount(5);
        List<com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto> topEvents = topEventIds.stream()
                .map(id -> {
                    Event event = eventRepository.findById(id).orElse(null);
                    if (event == null) return null;
                    long count = ((event.getRegistrations() != null ? event.getRegistrations().size() : 0) +
                            (event.getRegistrationsV2() != null ? event.getRegistrationsV2().size() : 0));
                    return com.merbsconnect.events.dto.response.RegistrationStatsResponse.TopEventDto.builder()
                            .eventTitle(event.getTitle())
                            .registrationCount(count)
                            .build();
                })
                .filter(java.util.Objects::nonNull)
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

    @Override
    @Transactional
    public String uploadEventImage(Long eventId, MultipartFile image) throws IOException {
        Event event = getEventByIdInternal(eventId);

        // Delete old image if exists
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            try {
                String key = storageService.extractKeyFromUrl(event.getImageUrl());
                storageService.deleteFile(key);
                log.info("Deleted old event image: {}", event.getImageUrl());
            } catch (Exception e) {
                log.warn("Failed to delete old event image: {}", e.getMessage());
            }
        }

        // Upload new image
        String imageUrl = storageService.uploadGalleryItem(eventId, image, MediaType.IMAGE);

        event.setImageUrl(imageUrl);
        eventRepository.save(event);

        log.info("Uploaded new event image for event ID {}: {}", eventId, imageUrl);

        return imageUrl;
    }

    @Override
    @Transactional
    public MessageResponse updateRegistration(Long eventId, Long registrationId, EventRegistrationDto registrationDto) {
        // Verify event exists
        getEventByIdInternal(eventId);

        com.merbsconnect.events.model.EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("Registration not found with id: " + registrationId));

        if (!registration.getEvent().getId().equals(eventId)) {
            throw new BusinessException("Registration does not belong to the specified event");
        }

        // Update basic fields
        if (registrationDto.getName() != null && !registrationDto.getName().isBlank()) {
            registration.setName(registrationDto.getName());
        }
        if (registrationDto.getEmail() != null && !registrationDto.getEmail().isBlank()) {
            // Check uniqueness if email changed
            if (!registration.getEmail().equalsIgnoreCase(registrationDto.getEmail()) &&
                    eventRegistrationRepository.existsByEventIdAndEmail(eventId, registrationDto.getEmail())) {
                throw new BusinessException("Email already pending/registered for this event");
            }
            registration.setEmail(registrationDto.getEmail());
        }
        if (registrationDto.getPhone() != null) {
            registration.setPhone(registrationDto.getPhone());
        }
        if (registrationDto.getNote() != null) {
            registration.setNote(registrationDto.getNote());
        }

        // Note: Updating shirt preference/orders is complex and left out for V1 of Edit.
        // Can be added if needed.

        eventRegistrationRepository.save(registration);
        log.info("Updated registration ID: {}", registrationId);

        return MessageResponse.builder()
                .message("Registration updated successfully")
                .build();
    }

    @Override
    @Transactional
    @CacheEvict(value = { "events", "registrations" }, key = "#eventId")
    public MessageResponse deleteRegistrationById(Long eventId, Long registrationId) {
        // Verify event exists
        getEventByIdInternal(eventId);

        com.merbsconnect.events.model.EventRegistration registration = eventRegistrationRepository.findById(registrationId)
                .orElseThrow(() -> new BusinessException("Registration not found with id: " + registrationId));

        if (!registration.getEvent().getId().equals(eventId)) {
            throw new BusinessException("Registration does not belong to the specified event");
        }

        eventRegistrationRepository.delete(registration);
        log.info("Deleted registration ID: {} from event ID: {}", registrationId, eventId);

        return MessageResponse.builder()
                .message("Registration deleted successfully")
                .build();
    }
}
