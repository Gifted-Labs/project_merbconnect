package com.merbsconnect.events.repository;

import com.merbsconnect.events.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

        boolean existsByTitleAndDate(String title, LocalDate date);

        long countByDateAfter(LocalDate date);

        long countByDateBefore(LocalDate date);

        List<Event> findByDateBetween(LocalDate startDate, LocalDate endDate);

        @Query(value = "SELECT event_id FROM (" +
                        "SELECT event_id FROM event_registrations " +
                        "UNION ALL " +
                        "SELECT event_id FROM event_registrations_v2" +
                        ") combined GROUP BY event_id ORDER BY COUNT(*) DESC LIMIT :limit", nativeQuery = true)
        List<Long> findTopEventIdsByRegistrationCount(@Param("limit") int limit);

        @Override
        org.springframework.data.domain.Page<Event> findAll(org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {
                        "speakersV2", "speakers", "itinerary", "reviews", "articles",
                        "galleryItems", "registrationsV2", "testimonials", "sponsors", "contacts"
        })
        @Query("SELECT e FROM Event e WHERE e.id = :id")
        java.util.Optional<Event> findWithDetailsById(@Param("id") Long id);

        Page<Event> findEventByDateAfter(LocalDate dateAfter, Pageable pageable);

        Page<Event> findEventByDateBefore(LocalDate dateBefore, Pageable pageable);

        @Query("SELECT e FROM Event e WHERE YEAR(e.date) = :year")
        Optional<Event> findEventByYear(@Param("year") Long year);

        @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Event e WHERE YEAR(e.date) = :year")
        boolean existsEventByYear(Long year);

        @Query("SELECT e FROM Event e WHERE e.location = :location AND e.date = :date")
        List<Event> findConflictingEvents(
                        @Param("location") String location,
                        @Param("date") LocalDate date);

        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Modifying
        @Query(value = "DELETE FROM event_sponsors WHERE event_id = :eventId", nativeQuery = true)
        void deleteEventSponsors(@Param("eventId") Long eventId);

        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Modifying
        @Query(value = "DELETE FROM event_speakers WHERE event_id = :eventId", nativeQuery = true)
        void deleteEventSpeakers(@Param("eventId") Long eventId);

        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Modifying
        @Query(value = "DELETE FROM event_contacts WHERE event_id = :eventId", nativeQuery = true)
        void deleteEventContacts(@Param("eventId") Long eventId);

        @org.springframework.transaction.annotation.Transactional
        @org.springframework.data.jpa.repository.Modifying
        @Query(value = "DELETE FROM event_registrations WHERE event_id = :eventId", nativeQuery = true)
        void deleteEventRegistrations(@Param("eventId") Long eventId);
}
