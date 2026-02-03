package com.merbsconnect.util.mapper;

import com.merbsconnect.dto.response.PageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.response.EventItineraryItemResponse;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.dto.response.EventSpeakerResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.EventItineraryItem;
import com.merbsconnect.events.model.EventSpeaker;
import com.merbsconnect.events.model.Registration;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EventMapper {

    private EventMapper() {
        // Private constructor to prevent instantiation
    }

    public static Event mapToEvent(CreateEventRequest request) {
        return Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .date(request.getDate())
                .time(request.getTime())
                .imageUrl(request.getImageUrl())
                .speakers(request.getSpeakers())
                .sponsors(request.getSponsors())
                .contacts(request.getContacts())
                .theme(request.getTheme())
                .build();
    }

    public static EventResponse mapToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .date(event.getDate())
                .time(event.getTime())
                .imageUrl(event.getImageUrl())
                .speakers(event.getSpeakers())
                .sponsors(event.getSponsors())
                .contacts(event.getContacts())
                .theme(event.getTheme())
                .speakersV2(mapSpeakersV2(event.getSpeakersV2()))
                .itinerary(mapItinerary(event.getItinerary()))
                .build();
    }

    public static Registration mapToRegistration(EventRegistrationDto registrationDto) {
        return Registration.builder()
                .name(registrationDto.getName())
                .email(registrationDto.getEmail())
                .phone(registrationDto.getPhone())
                .note(registrationDto.getNote())
                .program(registrationDto.getProgram())
                .academicLevel(registrationDto.getAcademicLevel())
                .university(registrationDto.getUniversity())
                .referralSource(registrationDto.getReferralSource())
                .referralSourceOther(registrationDto.getReferralSourceOther())
                .build();
    }

    public static <T> PageResponse<T> convertToPageResponse(Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    // ===== Helper Methods for V2 Mappings =====

    private static java.util.Set<EventSpeakerResponse> mapSpeakersV2(java.util.Collection<EventSpeaker> speakers) {
        if (speakers == null || speakers.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return speakers.stream()
                .map(EventMapper::mapSpeakerToResponse)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private static EventSpeakerResponse mapSpeakerToResponse(EventSpeaker speaker) {
        return EventSpeakerResponse.builder()
                .id(speaker.getId())
                .name(speaker.getName())
                .title(speaker.getTitle())
                .bio(speaker.getBio())
                .imageUrl(speaker.getImageUrl())
                .linkedinUrl(speaker.getLinkedinUrl())
                .twitterUrl(speaker.getTwitterUrl())
                .displayOrder(speaker.getDisplayOrder())
                .createdAt(speaker.getCreatedAt())
                .updatedAt(speaker.getUpdatedAt())
                .build();
    }

    private static java.util.Set<EventItineraryItemResponse> mapItinerary(
            java.util.Collection<EventItineraryItem> items) {
        if (items == null || items.isEmpty()) {
            return java.util.Collections.emptySet();
        }
        return items.stream()
                .map(EventMapper::mapItineraryItemToResponse)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }

    private static EventItineraryItemResponse mapItineraryItemToResponse(EventItineraryItem item) {
        return EventItineraryItemResponse.builder()
                .id(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .startTime(item.getStartTime())
                .endTime(item.getEndTime())
                .speakerName(item.getSpeakerName())
                .venue(item.getVenue())
                .displayOrder(item.getDisplayOrder())
                .itemType(item.getItemType())
                .itemTypeDisplayName(item.getItemType() != null ? item.getItemType().getDisplayName() : null)
                .durationMinutes(item.getDurationMinutes())
                .build();
    }
}
