package com.merbsconnect.events.service.impl;

import com.merbsconnect.authentication.dto.response.MessageResponse;
import com.merbsconnect.events.dto.response.*;
import com.merbsconnect.events.model.Event;
import com.merbsconnect.events.model.Registration;
import com.merbsconnect.events.model.Speaker;
import com.merbsconnect.events.repository.EventRepository;
import com.merbsconnect.exception.BusinessException;
import com.merbsconnect.sms.service.SmsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for EventServiceImpl admin dashboard methods.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EventServiceImpl Admin Dashboard Tests")
class EventServiceImplAdminTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private EventServiceImpl eventService;

    private Event testEvent;
    private Registration testRegistration;
    private Speaker testSpeaker;

    @BeforeEach
    void setUp() {
        // Setup test data
        testRegistration = Registration.builder()
                .email("test@example.com")
                .name("Test User")
                .phone("+1234567890")
                .build();

        testSpeaker = Speaker.builder()
                .name("John Doe")
                .bio("Expert speaker")
                .imageUrl("http://example.com/image.jpg")
                .build();

        testEvent = Event.builder()
                .id(1L)
                .title("Test Event")
                .description("Test Description")
                .location("Test Location")
                .date(LocalDate.now().plusDays(7))
                .registrations(new HashSet<>(Collections.singletonList(testRegistration)))
                .speakers(new HashSet<>(Collections.singletonList(testSpeaker)))
                .build();
    }

    @Test
    @DisplayName("getEventStats should return correct statistics")
    void testGetEventStats() {
        // Arrange
        List<Event> allEvents = Arrays.asList(testEvent);
        when(eventRepository.count()).thenReturn(1L);
        when(eventRepository.findEventByDateAfter(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(testEvent)));
        when(eventRepository.findEventByDateBefore(any(LocalDate.class), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(eventRepository.findAll()).thenReturn(allEvents);

        // Act
        EventStatsResponse stats = eventService.getEventStats();

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalEvents()).isEqualTo(1L);
        assertThat(stats.getUpcomingEvents()).isEqualTo(1L);
        assertThat(stats.getPastEvents()).isEqualTo(0L);
        assertThat(stats.getTotalRegistrations()).isEqualTo(1L);
        assertThat(stats.getAverageRegistrationsPerEvent()).isEqualTo(1.0);

        verify(eventRepository).count();
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getEventAnalytics should return analytics for existing event")
    void testGetEventAnalytics() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act
        EventAnalyticsResponse analytics = eventService.getEventAnalytics(1L);

        // Assert
        assertThat(analytics).isNotNull();
        assertThat(analytics.getEventId()).isEqualTo(1L);
        assertThat(analytics.getEventTitle()).isEqualTo("Test Event");
        assertThat(analytics.getTotalRegistrations()).isEqualTo(1L);
        assertThat(analytics.getSpeakerCount()).isEqualTo(1);
        assertThat(analytics.getEventStatus()).isEqualTo("UPCOMING");

        verify(eventRepository).findById(1L);
    }

    @Test
    @DisplayName("getEventAnalytics should throw exception for non-existent event")
    void testGetEventAnalyticsNotFound() {
        // Arrange
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> eventService.getEventAnalytics(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Event not found");

        verify(eventRepository).findById(999L);
    }

    @Test
    @DisplayName("deleteRegistration should remove registration successfully")
    void testDeleteRegistration() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        // Act
        MessageResponse response = eventService.deleteRegistration(1L, "test@example.com");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("deleted successfully");
        assertThat(testEvent.getRegistrations()).isEmpty();

        verify(eventRepository).findById(1L);
        verify(eventRepository).save(testEvent);
    }

    @Test
    @DisplayName("deleteRegistration should throw exception for non-existent email")
    void testDeleteRegistrationNotFound() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act & Assert
        assertThatThrownBy(() -> eventService.deleteRegistration(1L, "nonexistent@example.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Registration not found");

        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("getRegistrationStats should return stats for all events")
    void testGetRegistrationStats() {
        // Arrange
        List<Event> events = Collections.singletonList(testEvent);
        when(eventRepository.findAll()).thenReturn(events);

        // Act
        RegistrationStatsResponse stats = eventService.getRegistrationStats(null, null);

        // Assert
        assertThat(stats).isNotNull();
        assertThat(stats.getTotalRegistrations()).isEqualTo(1L);
        assertThat(stats.getRegistrationsByEvent()).hasSize(1);
        assertThat(stats.getTopEventsByRegistrations()).hasSize(1);

        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getEventSpeakers should return speakers list")
    void testGetEventSpeakers() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // Act
        List<Speaker> speakers = eventService.getEventSpeakers(1L);

        // Assert
        assertThat(speakers).isNotNull();
        assertThat(speakers).hasSize(1);
        assertThat(speakers.get(0).getName()).isEqualTo("John Doe");

        verify(eventRepository).findById(1L);
    }

    @Test
    @DisplayName("getDashboardData should return consolidated dashboard")
    void testGetDashboardData() {
        // Arrange
        List<Event> events = Collections.singletonList(testEvent);
        when(eventRepository.count()).thenReturn(1L);
        when(eventRepository.findEventByDateAfter(any(), any())).thenReturn(new PageImpl<>(events));
        when(eventRepository.findEventByDateBefore(any(), any())).thenReturn(Page.empty());
        when(eventRepository.findAll()).thenReturn(events);
        when(eventRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(events));

        // Act
        DashboardResponse dashboard = eventService.getDashboardData();

        // Assert
        assertThat(dashboard).isNotNull();
        assertThat(dashboard.getOverallStats()).isNotNull();
        assertThat(dashboard.getRecentEvents()).isNotEmpty();
        assertThat(dashboard.getTopEvents()).isNotEmpty();
        assertThat(dashboard.getSystemStatus()).isEqualTo("HEALTHY");
    }

    @Test
    @DisplayName("deleteMultipleRegistrations should delete all specified registrations")
    void testDeleteMultipleRegistrations() {
        // Arrange
        Registration reg2 = Registration.builder()
                .email("test2@example.com")
                .name("Test User 2")
                .phone("+9876543210")
                .build();
        testEvent.getRegistrations().add(reg2);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        List<String> emailsToDelete = Arrays.asList("test@example.com", "test2@example.com");

        // Act
        MessageResponse response = eventService.deleteMultipleRegistrations(1L, emailsToDelete);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("2 registration(s)");
        assertThat(testEvent.getRegistrations()).isEmpty();

        verify(eventRepository).findById(1L);
        verify(eventRepository).save(testEvent);
    }

    @Test
    @DisplayName("deleteMultipleRegistrations should handle partial matches")
    void testDeleteMultipleRegistrationsPartial() {
        // Arrange
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        List<String> emailsToDelete = Arrays.asList("test@example.com", "nonexistent@example.com");

        // Act
        MessageResponse response = eventService.deleteMultipleRegistrations(1L, emailsToDelete);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).contains("1 registration(s)");

        verify(eventRepository).findById(1L);
        verify(eventRepository).save(testEvent);
    }
}
