package ru.practicum.services.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.events.model.StatusEvent;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RepositoryEvent extends JpaRepository<Event, Long> {

    @Query(value = "select ev.* from events as ev where initiator_id = ?1 ", nativeQuery = true)
    Page<Event> findByInitiator_id(Long userId, Pageable pageable);


    @Query(value = "select e from Event as e " +
            "where e.eventDate BETWEEN :rangeStart AND :rangeEnd " +
            "AND (coalesce(:users, null) is null or (e.initiator.id in :users)) " +
            "AND (coalesce(:state, null) is null or (e.state in :state)) " +
            "AND (coalesce(:categories, null) is null or (e.category in :categories)) ")
    Page<Event> findEventForAdmin(LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  List<Long> users,
                                  List<String> state,
                                  List<Long> categories,
                                  Pageable pageable);

    @Query(value = "SELECT e " +
            "FROM Event e " +
            "WHERE e.state = 'PUBLISHED'  AND e.paid = :paid " +
            "AND (coalesce(:categories, null) is null or (e.category.id in :categories)) " +
            "AND e.eventDate between :rangeStart AND :rangeEnd " +
            "order by e.eventDate")
    Page<Event> findEventForPublic(Boolean paid, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query(value = "SELECT e " +
            "FROM Event e " +
            "WHERE e.state = 'PUBLISHED' AND e.paid = :paid " +
            "AND ((upper(e.annotation) like upper(concat('%', :text, '%'))) OR (upper(e.description) like upper(concat('%', :text, '%')))) " +
            "AND (coalesce(:categories, null) is null or (e.category.id in :categories)) " +
            "AND e.eventDate between :rangeStart AND :rangeEnd " +
            "order by e.eventDate")
    Page<Event> findEventForPublicWithText(Boolean paid, String text, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}
