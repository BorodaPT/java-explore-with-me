package ru.practicum.services.events;

import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.dto.ParticipationRequestDto;
import events.enum_events.SortEvent;
import events.model.UpdateEventUserRequest;
import org.springframework.transaction.annotation.Transactional;
import events.model.EventRequestStatusUpdateRequest;
import events.model.EventRequestStatusUpdateResult;
import events.model.UpdateEventAdminRequest;
import ru.practicum.services.events.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface ServiceEvent {

    Event getEvent(Long eventId);

    //user
    @Transactional
    EventFullDto createEvent(Long userId, NewEventDto event);

    @Transactional
    EventFullDto editEvent(Long userId, Long idEvent, UpdateEventUserRequest event);

    @Transactional
    EventRequestStatusUpdateResult editStatus(Long userId, Long idEvent, EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestFromEvent(Long userId, Long eventId);

    //admin
    List<EventFullDto> getEventForAdmin(List<Integer> users,
                                        List<String> state,
                                        List<Integer> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    @Transactional
    EventFullDto editEventAdmin(Long id, UpdateEventAdminRequest updateEventAdminRequest);

    //public
    List<EventShortDto> getEventsPublic(String text,
                                       List<Integer> categories,
                                       Boolean paid,
                                       LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd,
                                       Boolean onlyAvailable,
                                       SortEvent sort,
                                       Integer from,
                                       Integer size,
                                       HttpServletRequest request);

    EventFullDto getEventPublicForUserById(Long id, HttpServletRequest request);

    //compilation

    List<Event> getListEventForCompilation(List<Long> events);

    List<EventShortDto> getListEventDtoForCompilation(List<Event> events);
}