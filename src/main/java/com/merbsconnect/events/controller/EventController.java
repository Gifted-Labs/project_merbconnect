package com.merbsconnect.events.controller;

import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.request.SendBulkSmsToRegistrationsRequest;
import com.merbsconnect.events.dto.request.UpdateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Registration;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.service.EventService;
import com.merbsconnect.exception.BusinessException;

import com.merbsconnect.sms.dtos.request.BulkSmsRequest;
import com.merbsconnect.sms.dtos.request.CreateTemplateRequest;
import com.merbsconnect.sms.dtos.response.BulkSmsResponse;
import com.merbsconnect.sms.dtos.response.TemplateResponse;
import com.merbsconnect.sms.service.SmsService;
import jakarta.servlet.http.HttpServletResponse;
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
@CrossOrigin(origins = {"http://localhost:8080","http://localhost:5173"}, allowedHeaders = {"*"}, maxAge = 3600)
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);
    private final EventService eventService;
    private final SmsService smsService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(@RequestBody CreateEventRequest eventRequest) {
        try {
            EventResponse eventResponse = eventService.createEvent(eventRequest);
            return new ResponseEntity<>(eventResponse, HttpStatus.CREATED);
        } catch (BusinessException e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventRequest eventRequest) {
        try {
            EventResponse eventResponse = eventService.updateEvent(eventRequest, eventId);
            return new ResponseEntity<>(eventResponse, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MessageResponse> deleteEvent(@PathVariable Long eventId) {
        try {
            MessageResponse response = eventService.deleteEvent(eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getAllEvents(Pageable pageable) {
        Page<EventResponse> events = eventService.getAllEvents(pageable);
        return new ResponseEntity<>(events, HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
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

    @PostMapping("/{eventId}/speakers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addSpeakerToEvent(@PathVariable Long eventId, @RequestBody Speaker speaker) {
        try {
            MessageResponse response = eventService.addSpeakerToEvent(speaker, eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{eventId}/speakers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> removeSpeakerFromEvent(@PathVariable Long eventId, @RequestParam(value = "speakerName", required = false) String speakerName) {
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
    
    @PutMapping("/{eventId}/speakers/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> updateEventSpeaker(@PathVariable Long eventId, @RequestBody Speaker speaker) {
        try {
            MessageResponse response = eventService.updateEventSpeaker(speaker, eventId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            return new ResponseEntity<>(new MessageResponse("Update not successful"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{eventId}/registrations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<Registration>> getEventRegistrations(
            @PathVariable Long eventId, Pageable pageable) {
            Page<Registration> registrations = eventService.getEventRegistrations(eventId, pageable);
            PageResponse<Registration> response = convertToPageResponse(registrations);
            return new ResponseEntity<>(response, HttpStatus.OK);

    }


    @PostMapping("/{eventId}/register")
    public ResponseEntity<MessageResponse> registerForEvent(@PathVariable Long eventId, @RequestBody EventRegistrationDto eventRegistrationDto) {
        try {
            log.info("Registering for event ID: {}, Registration Details: {}", eventId, eventRegistrationDto);
            MessageResponse response = eventService.registerForEvent(eventId, eventRegistrationDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (BusinessException e) {
            MessageResponse response = new MessageResponse(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("{eventId}/registrations/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void downloadRegistrations(@PathVariable Long eventId, HttpServletResponse response) {
        try{
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition","attachment; filename=\"registrations_" +eventId +".csv\"");
            
            eventService.writeRegistrationsToCsv(eventId, response.getOutputStream());
            response.flushBuffer();
            MessageResponse message = new MessageResponse("Registrations exported successfully.");
//            return new ResponseEntity<>(message,HttpStatus.OK);
        }catch (IOException e){
            throw new BusinessException("Failed to export registrations.");
        }
    }

    @PostMapping("/{eventId}/registrations/send-sms")
    @PreAuthorize("hasRole('ADMIN')")
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




}
