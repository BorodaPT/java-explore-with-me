package ru.practicum.services.events;

import dto.HitDto;
import dto.StatsDto;
import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.dto.ParticipationRequestDto;
import events.enum_events.SortEvent;
import events.enum_events.StatusAction;
import events.enum_events.StatusEvent;
import events.enum_events.StatusUserRequestEvent;
import events.model.EventRequestStatusUpdateRequest;
import events.model.EventRequestStatusUpdateResult;
import events.model.UpdateEventAdminRequest;
import events.model.UpdateEventUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.categories.ServiceCategory;
import ru.practicum.services.categories.model.Category;
import ru.practicum.services.events.mapper.MapperEvent;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.participants_request.ServiceParticipantsRequest;
import ru.practicum.services.participants_request.model.ParticipationRequest;
import ru.practicum.services.users.ServiceUser;
import ru.practicum.services.users.model.User;
import ru.practicum.statistics.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServiceEventImpl implements ServiceEvent {

    private final RepositoryEvent repositoryEvent;

    private final ServiceUser serviceUser;

    private final ServiceCategory serviceCategory;

    private final ServiceParticipantsRequest serviceParticipantsRequest;

    private final StatsClient statsClient;

    @Override
    public Event getEvent(Long eventId) {
        Event event = repositoryEvent.findById(eventId).orElseThrow(() -> new EwmException("Событие не найдено", "Event not found", HttpStatus.NOT_FOUND));
        return event;
    }

    //user
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEvent) {
        User user = serviceUser.getById(userId);
        Category category = serviceCategory.getById(newEvent.getCategory());
        Event event = MapperEvent.toNewEvent(newEvent, category, user);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.CONFLICT);
        }
        return MapperEvent.toNewEventFullDto(repositoryEvent.save(event));
    }

    @Override
    public EventFullDto editEvent(Long userId, Long idEvent, UpdateEventUserRequest event) {
        User user = serviceUser.getById(userId);
        Category category = serviceCategory.getById(event.getCategory().getId());

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.CONFLICT);
        }
        Event eventBase = getEvent(idEvent);
        if (eventBase.getState() == StatusEvent.PUBLISHED) {
            throw new EwmException("Событие уже запущено", "event is published", HttpStatus.CONFLICT);
        }

        eventBase.setAnnotation(event.getAnnotation());
        eventBase.setCategory(category);
        eventBase.setEventDate(event.getEventDate());
        eventBase.setDescription(event.getDescription());
        eventBase.setLocLat(event.getLocationEvent().getLat());
        eventBase.setLocLon(event.getLocationEvent().getLon());
        eventBase.setTitle(event.getTitle());
        if (event.getPaid() != null) {
            eventBase.setPaid(event.getPaid());
        }
        if (event.getRequestModeration() != null) {
            eventBase.setRequestModeration(event.getRequestModeration());
        }
        if (event.getRequestModeration() != null) {
            eventBase.setRequestModeration(event.getRequestModeration());
        }

        Event refEvent = repositoryEvent.saveAndFlush(eventBase);

        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(refEvent.getId());

        Long cntViews = getCountViews(refEvent.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + refEvent.getId()), false);

        return MapperEvent.toEventFullDto(refEvent, cntViews, cntRequest);
    }

    private Long getCountViews(LocalDateTime start, LocalDateTime end, List<String> events, Boolean unique) {
        ResponseEntity<Object> response = statsClient.getStats(start, end, events, unique);
        StatsDto statsDto = (StatsDto) response.getBody();
        return statsDto.getHits();
    }

    @Override
    public EventRequestStatusUpdateResult editStatus(Long userId, Long idEvent, EventRequestStatusUpdateRequest statusUpdateRequest) {
        Event eventBase = getEvent(idEvent);
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(eventBase.getId());
        if (eventBase.getParticipantLimit() != 0 && !eventBase.getRequestModeration()) {
            if (eventBase.getParticipantLimit().equals(cntRequest)) {
                throw new EwmException("Лимит одобренных заявок", "The participant limit has been reached", HttpStatus.CONFLICT);
            }
        }

        for (Long idRequest : statusUpdateRequest.getRequestIds()) {
            ParticipationRequest request = serviceParticipantsRequest.getRequest(idRequest);

            if (request.getStatus() != StatusUserRequestEvent.PENDING) {
                throw new EwmException("Статус заявки не в ожидании", "Status not pending", HttpStatus.CONFLICT);
            }

            if (statusUpdateRequest.getStatus() == StatusUserRequestEvent.CONFIRMED && eventBase.getParticipantLimit() != 0) {
                if (eventBase.getParticipantLimit().equals(cntRequest)) {
                    serviceParticipantsRequest.editRequestStatus(idRequest, StatusUserRequestEvent.REJECTED);
                } else {
                    serviceParticipantsRequest.editRequestStatus(idRequest, statusUpdateRequest.getStatus());
                    cntRequest++;
                }
            } else {
                serviceParticipantsRequest.editRequestStatus(idRequest, statusUpdateRequest.getStatus());
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(serviceParticipantsRequest.getRequestsByStatus(idEvent, StatusUserRequestEvent.CONFIRMED));
        result.setRejectedRequests(serviceParticipantsRequest.getRequestsByStatus(idEvent, StatusUserRequestEvent.REJECTED));
        return result;
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        User user = serviceUser.getById(userId);
        List<Event> events = repositoryEvent.findByInitiator_id(userId, PageRequest.of(from, size)).getContent();
        List<EventShortDto> results = new ArrayList<>();
        if (events.size() != 0) {
            for (Event event : events) {
                Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
                Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
                results.add(MapperEvent.toEventShortDto(event, cntViews, cntRequest));
            }
        }
        return results;
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        User user = serviceUser.getById(userId);
        Event event = getEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new EwmException("Пользователь не является владельцем события", "User is not owner event", HttpStatus.CONFLICT);
        }
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
        return MapperEvent.toEventFullDto(event, cntViews, cntRequest);
    }

    @Override
    public List<ParticipationRequestDto> getRequestFromEvent(Long userId, Long eventId) {
        User user = serviceUser.getById(userId);
        Event event = getEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new EwmException("Пользователь не является владельцем события", "User is not owner event", HttpStatus.CONFLICT);
        }
        return serviceParticipantsRequest.getRequestsFromEvent(eventId);
    }

    //admin
    @Override
    public List<EventFullDto> getEventForAdmin(List<Integer> users, List<String> state, List<Integer> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<EventFullDto> resultEvents = new ArrayList<>();
        List<Event> events = repositoryEvent.findEventForAdmin(rangeStart, rangeEnd, users, state, categories, PageRequest.of(from, size)).getContent();
        if (events.size() != 0) {
            for (Event event : events) {
                Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
                Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
                resultEvents.add(MapperEvent.toEventFullDto(event, cntViews, cntRequest));
            }
        }
        return resultEvents;
    }

    @Override
    public EventFullDto editEventAdmin(Long id, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = getEvent(id);
        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().equals("")) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = serviceCategory.getById(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().equals("")) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.CONFLICT);
            }
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocationEvent() != null) {
            event.setLocLat(updateEventAdminRequest.getLocationEvent().getLat());
            event.setLocLon(updateEventAdminRequest.getLocationEvent().getLon());
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction() == StatusAction.PUBLISH_EVENT) {
                if (event.getState() == StatusEvent.PENDING) {
                    event.setState(StatusEvent.PUBLISHED);
                } else {
                    throw new EwmException("Событие не удовлетворяет правилам редактирования", "Событие не находится в состоянии ожидания", HttpStatus.CONFLICT);
                }
            } else if (updateEventAdminRequest.getStateAction() == StatusAction.REJECT_EVENT) {
                if (event.getState() == StatusEvent.PENDING) {
                    event.setState(StatusEvent.CANCELED);
                } else {
                    throw new EwmException("Событие не удовлетворяет правилам редактирования",
                            (event.getState() == StatusEvent.PUBLISHED) ? "Событие уже опубликовано" : "Событие уже отменено", HttpStatus.CONFLICT);
                }
            }
        }
        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().equals("")) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        Event refEvent = repositoryEvent.saveAndFlush(event);
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
        return MapperEvent.toEventFullDto(refEvent, cntViews, cntRequest);
    }

    //public
    @Override
    public List<EventShortDto> getEventsPublic(String text, List<Integer> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, SortEvent sort, Integer from, Integer size, HttpServletRequest request) {
        statsClient.postHit(new HitDto("ewm_service", request.getRequestURI(), request.getRemoteAddr()));
        LocalDateTime start;
        LocalDateTime end;
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new EwmException("Дата начала больше даты окончания", "Дата начала больше даты окончания", HttpStatus.BAD_REQUEST);
            } else {
                start = rangeStart;
                end = rangeEnd;
            }
        } else {
            start = LocalDateTime.now().plusSeconds(1);
            end = LocalDateTime.now().plusYears(2);
        }
        List<Event> events = repositoryEvent.findEventForPublic(paid, text, categories, start, end, PageRequest.of(from, size)).getContent();
        List<EventShortDto> results = new ArrayList<>();
        if (events.size() != 0) {
            //Collections.sort(events, (e1, e2) -> e1.getEventDate().c);
            for (Event event : events) {
                Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
                Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
                results.add(MapperEvent.toEventShortDto(event, cntViews, cntRequest));
            }
            if (sort == SortEvent.VIEWS) {
                Collections.sort(results, new Comparator<EventShortDto>() {
                    @Override
                    public int compare(EventShortDto a1, EventShortDto a2) {
                        return (a1.getViews() < a2.getViews()) ? -1 : ((a1.getViews() == a2.getViews()) ? 0 : 1);
                    }
                });
            }
        }
        return results;
    }

    @Override
    public EventFullDto getEventPublicForUserById(Long id, HttpServletRequest request) {
        statsClient.postHit(new HitDto("ewm_service", request.getRequestURI(), request.getRemoteAddr()));
        Event event = getEvent(id);
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
        return MapperEvent.toEventFullDto(event, cntViews, cntRequest);
    }

    //compilation
    @Override
    public List<EventShortDto> getListEventDtoForCompilation(List<Event> events) {
        List<EventShortDto> resultEvents = new ArrayList<>();
        for (Event event : events) {
            Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
            Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
            resultEvents.add(MapperEvent.toEventShortDto(event, cntViews, cntRequest));
        }
        return resultEvents;
    }

    @Override
    public List<Event> getListEventForCompilation(List<Long> events) {
        List<Event> resultEvents = new ArrayList<>();
        for (Long eventId : events) {
            resultEvents.add(getEvent(eventId));
        }
        return resultEvents;
    }
}
