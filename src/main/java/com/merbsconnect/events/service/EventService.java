package com.merbsconnect.events.service;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.SendBulkSmsToRegistrationsRequest;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Registration;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public interface EventService {

    EventResponse createEvent(CreateEventRequest eventRequest);

    EventResponse updateEvent(UpdateEventRequest eventRequest, Long eventId);

    MessageResponse deleteEvent(Long eventId);

    Page<EventResponse> getAllEvents(Pageable pageable);

    Optional<EventResponse> getEventById(Long eventId);

    Optional<EventResponse> getEventByYear(Long year);

    MessageResponse addSpeakerToEvent(Speaker speaker, Long eventId);

    MessageResponse removeSpeakerFromEvent(Long eventId, String speakerName);

    Page<EventResponse> getUpcomingEvents(Pageable pageable);

    Page<EventResponse> getPastEvents(Pageable pageable);

    MessageResponse updateEventSpeaker(Speaker speaker, Long eventId);

    MessageResponse registerForEvent(Long eventId, EventRegistrationDto registrationDto);

    Page<EventRegistrationDto> getEventRegistrations(Long eventId, Pageable pageable, String search,
            Boolean checkInStatus, String shirtSize);

    void writeRegistrationsToCsv(Long eventId, OutputStream outputStream) throws IOException;

    BulkSmsResponse sendBulkSmsToSelectedRegistrations(SendBulkSmsToRegistrationsRequest request);

    // Admin Dashboard Endpoints
    com.merbsconnect.events.dto.response.EventStatsResponse getEventStats();

    com.merbsconnect.events.dto.response.EventAnalyticsResponse getEventAnalytics(Long eventId);

    MessageResponse deleteRegistration(Long eventId, String email);

    MessageResponse deleteRegistrationById(Long eventId, Long registrationId);

    com.merbsconnect.events.dto.response.RegistrationStatsResponse getRegistrationStats(
            java.time.LocalDate startDate, java.time.LocalDate endDate);

    java.util.List<Speaker> getEventSpeakers(Long eventId);

    com.merbsconnect.events.dto.response.DashboardResponse getDashboardData();

    MessageResponse deleteMultipleRegistrations(Long eventId, java.util.List<String> emails);

    String uploadEventImage(Long eventId, org.springframework.web.multipart.MultipartFile image) throws IOException;

    MessageResponse updateRegistration(Long eventId, Long registrationId, EventRegistrationDto registrationDto);
}
