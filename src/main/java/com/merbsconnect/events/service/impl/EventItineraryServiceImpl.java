package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.request.EventItineraryItemRequest;
import com.merbsconnect.events.dto.response.EventItineraryItemResponse;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.EventItineraryItem;
import com.merbsconnect.events.model.ItineraryItemType;
import com.merbsconnect.events.repository.EventItineraryItemRepository;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.events.service.EventItineraryService;
import com.merbsconnect.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing event itinerary/program lineup.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventItineraryServiceImpl implements EventItineraryService {

    private final EventItineraryItemRepository itineraryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventItineraryItemResponse addItineraryItem(Long eventId, EventItineraryItemRequest request) {
        Event event = getEventOrThrow(eventId);

        EventItineraryItem item = EventItineraryItem.builder()
                .event(event)
                .title(request.getTitle())
                .description(request.getDescription())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .speakerName(request.getSpeakerName())
                .venue(request.getVenue())
                .displayOrder(
                        request.getDisplayOrder() != null ? request.getDisplayOrder() : getNextDisplayOrder(eventId))
                .itemType(request.getItemType() != null ? request.getItemType() : ItineraryItemType.SESSION)
                .durationMinutes(calculateDuration(request))
                .build();

        EventItineraryItem saved = itineraryRepository.save(item);
        log.info("Added itinerary item '{}' to event ID {}", saved.getTitle(), eventId);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public EventItineraryItemResponse updateItineraryItem(Long eventId, Long itemId,
            EventItineraryItemRequest request) {
        EventItineraryItem item = getItemOrThrow(eventId, itemId);

        if (request.getTitle() != null) {
            item.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getStartTime() != null) {
            item.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            item.setEndTime(request.getEndTime());
        }
        if (request.getSpeakerName() != null) {
            item.setSpeakerName(request.getSpeakerName());
        }
        if (request.getVenue() != null) {
            item.setVenue(request.getVenue());
        }
        if (request.getDisplayOrder() != null) {
            item.setDisplayOrder(request.getDisplayOrder());
        }
        if (request.getItemType() != null) {
            item.setItemType(request.getItemType());
        }

        // Recalculate duration if times changed
        Integer duration = calculateDuration(request);
        if (duration != null) {
            item.setDurationMinutes(duration);
        } else if (request.getDurationMinutes() != null) {
            item.setDurationMinutes(request.getDurationMinutes());
        }

        EventItineraryItem updated = itineraryRepository.save(item);
        log.info("Updated itinerary item ID {} for event ID {}", itemId, eventId);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public MessageResponse deleteItineraryItem(Long eventId, Long itemId) {
        EventItineraryItem item = getItemOrThrow(eventId, itemId);

        itineraryRepository.delete(item);
        log.info("Deleted itinerary item ID {} from event ID {}", itemId, eventId);

        return MessageResponse.builder()
                .message("Itinerary item deleted successfully")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventItineraryItemResponse> getItinerary(Long eventId) {
        // Verify event exists
        getEventOrThrow(eventId);

        return itineraryRepository.findByEventIdOrderByDisplayOrderAscStartTimeAsc(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventItineraryItemResponse getItineraryItemById(Long eventId, Long itemId) {
        return mapToResponse(getItemOrThrow(eventId, itemId));
    }

    @Override
    @Transactional
    public MessageResponse reorderItinerary(Long eventId, List<Long> itemIds) {
        // Verify event exists
        getEventOrThrow(eventId);

        List<EventItineraryItem> items = itineraryRepository.findByEventIdOrderByDisplayOrderAscStartTimeAsc(eventId);

        // Create a map for quick lookup
        java.util.Map<Long, EventItineraryItem> itemMap = items.stream()
                .collect(Collectors.toMap(EventItineraryItem::getId, item -> item));

        // Update display orders based on the provided order
        int order = 0;
        for (Long itemId : itemIds) {
            EventItineraryItem item = itemMap.get(itemId);
            if (item != null) {
                item.setDisplayOrder(order++);
            }
        }

        itineraryRepository.saveAll(items);
        log.info("Reordered {} itinerary items for event ID {}", itemIds.size(), eventId);

        return MessageResponse.builder()
                .message("Itinerary reordered successfully")
                .build();
    }

    @Override
    @Transactional
    public List<EventItineraryItemResponse> bulkAddItineraryItems(Long eventId,
            List<EventItineraryItemRequest> requests) {
        Event event = getEventOrThrow(eventId);

        List<EventItineraryItem> items = new ArrayList<>();
        int startOrder = getNextDisplayOrder(eventId);

        for (int i = 0; i < requests.size(); i++) {
            EventItineraryItemRequest request = requests.get(i);
            EventItineraryItem item = EventItineraryItem.builder()
                    .event(event)
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .startTime(request.getStartTime())
                    .endTime(request.getEndTime())
                    .speakerName(request.getSpeakerName())
                    .venue(request.getVenue())
                    .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : startOrder + i)
                    .itemType(request.getItemType() != null ? request.getItemType() : ItineraryItemType.SESSION)
                    .durationMinutes(calculateDuration(request))
                    .build();
            items.add(item);
        }

        List<EventItineraryItem> saved = itineraryRepository.saveAll(items);
        log.info("Bulk added {} itinerary items to event ID {}", saved.size(), eventId);

        return saved.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ===== Helper Methods =====

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BusinessException("Event not found with ID: " + eventId));
    }

    private EventItineraryItem getItemOrThrow(Long eventId, Long itemId) {
        EventItineraryItem item = itineraryRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException("Itinerary item not found with ID: " + itemId));

        if (!item.getEvent().getId().equals(eventId)) {
            throw new BusinessException("Itinerary item does not belong to event ID: " + eventId);
        }

        return item;
    }

    private int getNextDisplayOrder(Long eventId) {
        List<EventItineraryItem> items = itineraryRepository.findByEventIdOrderByDisplayOrderAscStartTimeAsc(eventId);
        if (items.isEmpty()) {
            return 0;
        }
        return items.stream()
                .mapToInt(EventItineraryItem::getDisplayOrder)
                .max()
                .orElse(0) + 1;
    }

    private Integer calculateDuration(EventItineraryItemRequest request) {
        if (request.getStartTime() != null && request.getEndTime() != null) {
            long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
            return (int) minutes;
        }
        return request.getDurationMinutes();
    }

    private EventItineraryItemResponse mapToResponse(EventItineraryItem item) {
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
