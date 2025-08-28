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




}
