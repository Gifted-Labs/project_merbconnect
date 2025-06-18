package com.merbsconnect.util.mapper;

import com.merbsconnect.events.dto.request.CreateEventRequest;
import com.merbsconnect.events.dto.response.EventResponse;
import com.merbsconnect.events.model.Event;

public class EventMapper {

    public static Event mapToEvent(CreateEventRequest request){
        return Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .date(request.getDate())
                .time(request.getTime())
                .imageUrl(request.getImageUrl())
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
                .build();
    }
}
