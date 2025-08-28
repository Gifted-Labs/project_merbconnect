package com.merbsconnect.util.mapper;

import com.merbsconnect.academics.dto.response.PageResponse;
import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.request.EventRegistrationDto;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.Registration;
import org.springframework.data.domain.Page;

public class EventMapper {

    private EventMapper() {
        // Private constructor to prevent instantiation
    }

    public static Event mapToEvent(CreateEventRequest request){
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
                .build();
    }

    public static EventResponse mapToEventResponse(Event event){
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
                .build();
    }

    public static Registration mapToRegistration(EventRegistrationDto registrationDto){
        return Registration.builder()
                .name(registrationDto.getName())
                .email(registrationDto.getEmail())
                .phone(registrationDto.getPhone())
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

}
