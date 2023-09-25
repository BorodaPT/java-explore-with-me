package ru.practicum.services.events;

import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.dto.ParticipationRequestDto;
import ru.practicum.services.events.enum_events.SortEvent;
import ru.practicum.services.events.model.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    Event getEvent(Long eventId);

    //user
    EventFullDto createEvent(Long userId, NewEventDto event);

    EventFullDto editEvent(Long userId, Long idEvent, UpdateEventUserRequest event);

    EventRequestStatusUpdateResult editStatus(Long userId, Long idEvent, EventRequestStatusUpdateRequest statusUpdateRequest);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto getUserEvent(Long userId, Long eventId);

    List<ParticipationRequestDto> getRequestFromEvent(Long userId, Long eventId);

    //admin
    List<EventFullDto> getEventForAdmin(List<Long> users,
                                        List<StatusEvent> state,
                                        List<Long> categories,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Integer from,
                                        Integer size);

    EventFullDto editEventAdmin(Long id, UpdateEventAdminRequest updateEventAdminRequest);

    //public
    List<EventShortDto> getEventsPublic(String text,
                                        List<Long> categories,
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
