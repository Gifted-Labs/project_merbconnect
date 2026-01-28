package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.SponsorApplicationRequest;
import com.merbsconnect.events.dto.request.VolunteerApplicationRequest;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.SponsorApplication;
import com.merbsconnect.events.model.VolunteerApplication;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.repository.SponsorApplicationRepository;
import com.merbsconnect.events.repository.VolunteerApplicationRepository;
import com.merbsconnect.events.service.ApplicationService;
import com.merbsconnect.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final VolunteerApplicationRepository volunteerRepository;
    private final SponsorApplicationRepository sponsorRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public MessageResponse applyToVolunteer(VolunteerApplicationRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new BusinessException("Event not found"));

        VolunteerApplication application = VolunteerApplication.builder()
                .event(event)
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .motivation(request.getMotivation())
                .areaOfInterest(request.getAreaOfInterest())
                .build();

        volunteerRepository.save(application);

        return MessageResponse.builder()
                .message("Volunteer application submitted successfully!")
                .build();
    }

    @Override
    @Transactional
    public MessageResponse applyToSponsor(SponsorApplicationRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new BusinessException("Event not found"));

        SponsorApplication application = SponsorApplication.builder()
                .event(event)
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .sponsorshipLevel(request.getSponsorshipLevel())
                .message(request.getMessage())
                .build();

        sponsorRepository.save(application);

        return MessageResponse.builder()
                .message("Sponsor application submitted successfully!")
                .build();
    }
}
