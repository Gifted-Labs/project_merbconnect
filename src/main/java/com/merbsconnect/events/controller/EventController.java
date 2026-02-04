package com.merbsconnect.events.controller;

import com.merbsconnect.dto.response.PageResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.SendBulkSmsToRegistrationsRequest;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Registration;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.service.CheckInService;
import com.merbsconnect.events.service.EventService;
import com.merbsconnect.exception.BusinessException;

import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import com.merbsconnect.sms.dtos.response.TemplateResponse;
import com.merbsconnect.sms.service.SmsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

import static com.merbsconnect.util.mapper.EventMapper.convertToPageResponse;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@CrossOrigin(origins = { "http://localhost:8080", "http://localhost:5173", "https://merbsconnect.com",
        "https://src.merbsconnect.com",
        "https://startright.merbsconnect.com" }, allowedHeaders = { "*" }, maxAge = 3600)
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final SmsService smsService;
    private final CheckInService checkInService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest eventRequest) {
        try {
            log.info("Creating event with request: {}", eventRequest);
            EventResponse eventResponse = eventService.createEvent(eventRequest);
            return new ResponseEntity<>(eventResponse, HttpStatus.CREATED);
        } catch (BusinessException e) {
            log.error("Business exception creating event: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Unexpected error creating event: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{eventId:[0-9]+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long eventId,
            @Valid @RequestBody UpdateEventRequest eventRequest) {
        try {
            EventResponse eventResponse = eventService.updateEvent(eventRequest, eventId);
            return new ResponseEntity<>(eventResponse, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{eventId:[0-9]+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteEvent(@PathVariable Long eventId) {
        try {
            MessageResponse response = eventService.deleteEvent(eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/{eventId:[0-9]+}/image", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<java.util.Map<String, String>> uploadEventImage(
            @PathVariable Long eventId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile image) {
        try {
            log.info("Uploading image for event ID: {}", eventId);
            String imageUrl = eventService.uploadEventImage(eventId, image);
            return new ResponseEntity<>(java.util.Map.of("imageUrl", imageUrl), HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error uploading event image: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            log.error("IO error uploading event image: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(Pageable pageable) {
        Page<EventResponse> events = eventService.getAllEvents(pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{eventId:[0-9]+}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
        Optional<EventResponse> eventResponse = eventService.getEventById(eventId);
        return eventResponse.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<EventResponse> getEventByYear(@PathVariable Long year) {
        try {
            Optional<EventResponse> eventResponse = eventService.getEventByYear(year);
            return eventResponse.map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (BusinessException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{eventId:[0-9]+}/speakers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<MessageResponse> addSpeakerToEvent(@PathVariable Long eventId, @RequestBody Speaker speaker) {
        try {
            MessageResponse response = eventService.addSpeakerToEvent(speaker, eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{eventId:[0-9]+}/speakers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> removeSpeakerFromEvent(@PathVariable Long eventId,
            @RequestParam(value = "speakerName", required = false) String speakerName) {
        try {
            MessageResponse response = eventService.removeSpeakerFromEvent(eventId, speakerName);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponse>> getUpcomingEvents(Pageable pageable) {
        Page<EventResponse> events = eventService.getUpcomingEvents(pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/past")
    public ResponseEntity<Page<EventResponse>> getPastEvents(Pageable pageable) {
        Page<EventResponse> events = eventService.getPastEvents(pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @PutMapping("/{eventId:[0-9]+}/speakers/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<MessageResponse> updateEventSpeaker(@PathVariable Long eventId,
            @RequestBody Speaker speaker) {
        try {
            MessageResponse response = eventService.updateEventSpeaker(speaker, eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse("Update not successful"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{eventId:[0-9]+}/registrations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<PageResponse<Registration>> getEventRegistrations(
            @PathVariable Long eventId, Pageable pageable) {
        Page<Registration> registrations = eventService.getEventRegistrations(eventId, pageable);
        PageResponse<Registration> response = convertToPageResponse(registrations);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    /**
     * Register for an event (v2 - with QR code, SMS, and PDF ticket).
     * Both endpoints point to the same enhanced registration flow.
     */
    @PostMapping("/{eventId:[0-9]+}/register")
    public ResponseEntity<com.merbsconnect.events.dto.response.RegistrationDetailsResponse> registerForEventV2(
            @PathVariable Long eventId,
            @RequestBody EventRegistrationDto eventRegistrationDto) {
        try {
            log.info("Registering for event ID: {}, Registration Details: {}", eventId, eventRegistrationDto);
            // Delegate to CheckInService v2 for enhanced registration with QR code, SMS,
            // and PDF
            com.merbsconnect.events.dto.response.RegistrationDetailsResponse response = checkInService
                    .registerForEventV2(eventId, eventRegistrationDto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            log.error("Registration failed: {}", e.getMessage());
            throw new BusinessException(e.getMessage());
        }
    }

    @GetMapping("{eventId:[0-9]+}/registrations/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public void downloadRegistrations(@PathVariable Long eventId, HttpServletResponse response) {
        try {
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"registrations_" + eventId + ".csv\"");

            eventService.writeRegistrationsToCsv(eventId, response.getOutputStream());
            response.flushBuffer();
            MessageResponse message = new MessageResponse("Registrations exported successfully.");
            // return new ResponseEntity<>(message,HttpStatus.OK);
        } catch (IOException e) {
            throw new BusinessException("Failed to export registrations.");
        }
    }

    @PostMapping("/{eventId:[0-9]+}/registrations/send-sms")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<BulkSmsResponse> sendBulkSmsToSelectedRegistrations(
            @PathVariable Long eventId,
            @RequestBody SendBulkSmsToRegistrationsRequest request) {
        try {
            // Ensure the event ID in the path matches the request
            request.setEventId(eventId);

            log.info("Sending bulk SMS to selected registrations for event ID: {}", eventId);
            BulkSmsResponse response = eventService.sendBulkSmsToSelectedRegistrations(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Failed to send bulk SMS: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Endpoint to create an SMS template.
     *
     * @param request The request body containing the template title and content.
     * @return A ResponseEntity containing the created template response.
     */
    @PostMapping("/sms/template")
    public ResponseEntity<TemplateResponse> createTemplate(@RequestBody CreateTemplateRequest request) {
        try {
            TemplateResponse response = smsService.createTemplate(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint to fetch all templates.
     *
     * @return A ResponseEntity containing the list of templates.
     */
    @GetMapping("/sms/templates")

    public ResponseEntity<TemplateResponse> getTemplates() throws IOException, InterruptedException {
        TemplateResponse templateResponse = smsService.getAllTemplates();
        return new ResponseEntity<>(templateResponse, HttpStatus.OK);

    }

    @GetMapping("/sms/templates/{templateId}")
    public ResponseEntity<TemplateResponse> getTemplateById(@PathVariable String templateId) {
        try {
            TemplateResponse templateResponse = smsService.getTemplateById(templateId);
            return new ResponseEntity<>(templateResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sms/send-bulk-sms")
    public ResponseEntity<BulkSmsResponse> sendBulkSms(@RequestBody BulkSmsRequest bulkSmsRequest) {
        try {
            BulkSmsResponse response = smsService.sendBulkSms(bulkSmsRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ==================== ADMIN DASHBOARD ENDPOINTS ====================

    /**
     * Get overall event statistics for admin dashboard.
     * Requires ADMIN, SUPER_ADMIN, or SUPPORT_ADMIN role.
     *
     * @return ResponseEntity containing event statistics
     */
    @GetMapping("/admin/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<com.merbsconnect.events.dto.response.EventStatsResponse> getEventStats() {
        try {
            log.info("Fetching event statistics");
            com.merbsconnect.events.dto.response.EventStatsResponse stats = eventService.getEventStats();
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching event stats: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get detailed analytics for a specific event.
     * Requires ADMIN, SUPER_ADMIN, or SUPPORT_ADMIN role.
     *
     * @param eventId The ID of the event
     * @return ResponseEntity containing event analytics
     */
    @GetMapping("/{eventId:[0-9]+}/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<com.merbsconnect.events.dto.response.EventAnalyticsResponse> getEventAnalytics(
            @PathVariable Long eventId) {
        try {
            log.info("Fetching analytics for event ID: {}", eventId);
            com.merbsconnect.events.dto.response.EventAnalyticsResponse analytics = eventService
                    .getEventAnalytics(eventId);
            return new ResponseEntity<>(analytics, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Event not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("Error fetching event analytics: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a specific registration from an event by email.
     * Requires ADMIN or SUPER_ADMIN role.
     *
     * @param eventId The ID of the event
     * @param email   The email of the registration to delete
     * @return ResponseEntity containing success message
     */
    @DeleteMapping("/{eventId:[0-9]+}/registrations/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteRegistration(
            @PathVariable Long eventId,
            @PathVariable String email) {
        try {
            log.info("Deleting registration with email {} from event ID {}", email, eventId);
            MessageResponse response = eventService.deleteRegistration(eventId, email);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error deleting registration: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error deleting registration: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get registration statistics with optional date filtering.
     * Requires ADMIN, SUPER_ADMIN, or SUPPORT_ADMIN role.
     *
     * @param startDate Optional start date for filtering
     * @param endDate   Optional end date for filtering
     * @return ResponseEntity containing registration statistics
     */
    @GetMapping("/admin/registrations/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<com.merbsconnect.events.dto.response.RegistrationStatsResponse> getRegistrationStats(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        try {
            log.info("Fetching registration stats from {} to {}", startDate, endDate);
            com.merbsconnect.events.dto.response.RegistrationStatsResponse stats = eventService
                    .getRegistrationStats(startDate, endDate);
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching registration stats: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get all speakers for a specific event.
     * Requires ADMIN, SUPER_ADMIN, or SUPPORT_ADMIN role.
     *
     * @param eventId The ID of the event
     * @return ResponseEntity containing list of speakers
     */
    @GetMapping("/{eventId:[0-9]+}/speakers")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<java.util.List<Speaker>> getEventSpeakers(@PathVariable Long eventId) {
        try {
            log.info("Fetching speakers for event ID: {}", eventId);
            java.util.List<Speaker> speakers = eventService.getEventSpeakers(eventId);
            return new ResponseEntity<>(speakers, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Event not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error fetching speakers: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get consolidated dashboard data (cached for 5 minutes).
     * Requires ADMIN, SUPER_ADMIN, or SUPPORT_ADMIN role.
     *
     * @return ResponseEntity containing dashboard data
     */
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'SUPPORT_ADMIN')")
    public ResponseEntity<com.merbsconnect.events.dto.response.DashboardResponse> getDashboardData() {
        try {
            log.info("Fetching dashboard data");
            com.merbsconnect.events.dto.response.DashboardResponse dashboard = eventService.getDashboardData();
            return new ResponseEntity<>(dashboard, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching dashboard data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete multiple registrations from an event in bulk.
     * Requires ADMIN or SUPER_ADMIN role.
     *
     * @param eventId The ID of the event
     * @param request The bulk delete request containing list of emails
     * @return ResponseEntity containing success message with count
     */
    @DeleteMapping("/{eventId:[0-9]+}/registrations/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<MessageResponse> deleteMultipleRegistrations(
            @PathVariable Long eventId,
            @RequestBody com.merbsconnect.events.dto.request.BulkDeleteRequest request) {
        try {
            log.info("Bulk deleting {} registrations from event ID {}",
                    request.getRegistrationEmails().size(), eventId);
            MessageResponse response = eventService.deleteMultipleRegistrations(
                    eventId, request.getRegistrationEmails());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            log.error("Error in bulk delete: {}", e.getMessage());
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Error in bulk delete: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
