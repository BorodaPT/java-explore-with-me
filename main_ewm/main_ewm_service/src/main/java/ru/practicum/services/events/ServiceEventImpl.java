package ru.practicum.services.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.HitDto;
import dto.StatsDto;
import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.dto.ParticipationRequestDto;
import ru.practicum.services.events.model.EventRequestStatusUpdateRequest;
import ru.practicum.services.events.model.EventRequestStatusUpdateResult;
import ru.practicum.services.events.model.UpdateEventAdminRequest;
import ru.practicum.services.events.model.UpdateEventUserRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.categories.ServiceCategory;
import ru.practicum.services.categories.model.Category;
import ru.practicum.services.events.enum_events.SortEvent;
import ru.practicum.services.events.enum_events.StatusAction;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.events.enum_events.StatusUserRequestEvent;
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

    private final Logger log = LoggerFactory.getLogger(ServiceEventImpl.class);

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

        if (newEvent.getDescription() == null) {
            throw new EwmException("Не заполенно поле Description", "Description not found", HttpStatus.BAD_REQUEST);
        }

        if (newEvent.getTitle() == null) {
            throw new EwmException("Не заполенно поле Title", "Title not found", HttpStatus.BAD_REQUEST);
        }

        User user = serviceUser.getById(userId);
        Category category = serviceCategory.getById(newEvent.getCategory());
        Event event = MapperEvent.toNewEvent(newEvent, category, user);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.BAD_REQUEST);
        }
        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0L);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        return MapperEvent.toNewEventFullDto(repositoryEvent.save(event));
    }

    @Override
    public EventFullDto editEvent(Long userId, Long idEvent, UpdateEventUserRequest event) {
        log.info("Получение event {}", event);

        User user = serviceUser.getById(userId);

        Event eventBase = getEvent(idEvent);
        if (eventBase.getState() == StatusEvent.PUBLISHED) {
            throw new EwmException("Событие уже запущено", "event is published", HttpStatus.CONFLICT);
        }

        if (event.getAnnotation() != null) {
            eventBase.setAnnotation(event.getAnnotation());
        }
        if (event.getCategory() != null) {
            Category category = serviceCategory.getById(event.getCategory().getId());
            eventBase.setCategory(category);
        }
        if (event.getEventDate() != null) {
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.BAD_REQUEST);
            }
            eventBase.setEventDate(event.getEventDate());
        }
        if (event.getDescription() != null) {
            eventBase.setDescription(event.getDescription());
        }

        if (event.getLocationEvent() != null) {
            if (event.getLocationEvent().getLat() != null) {
                eventBase.setLocLat(event.getLocationEvent().getLat());
            }
            if (event.getLocationEvent().getLon() != null) {
                eventBase.setLocLon(event.getLocationEvent().getLon());
            }
        }
        if (event.getTitle() != null) {
            eventBase.setTitle(event.getTitle());
        }
        if (event.getPaid() != null) {
            eventBase.setPaid(event.getPaid());
        }
        if (event.getRequestModeration() != null) {
            eventBase.setRequestModeration(event.getRequestModeration());
        }
        if (event.getRequestModeration() != null) {
            eventBase.setRequestModeration(event.getRequestModeration());
        }

        if (event.getStateAction() != null) {
            switch (event.getStateAction()) {
                case SEND_TO_REVIEW:
                    eventBase.setState(StatusEvent.PENDING);
                    break;
                case CANCEL_REVIEW:
                    eventBase.setState(StatusEvent.CANCELED);
                    break;
                default:
                    throw new EwmException("Недопустимое значения статуса события", "Incorrect status event", HttpStatus.BAD_REQUEST);
            }

        }
        Event refEvent = repositoryEvent.saveAndFlush(eventBase);

        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(refEvent.getId());

        Long cntViews = getCountViews(refEvent.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + refEvent.getId()), false);

        return MapperEvent.toEventFullDto(refEvent, cntViews, cntRequest);
    }

    private Long getCountViews(LocalDateTime start, LocalDateTime end, List<String> events, Boolean unique) {
        ResponseEntity<Object> response = statsClient.getStats(start, end, events, unique);

        ObjectMapper mapper = new ObjectMapper();
        List<StatsDto> statsDtos = mapper.convertValue(response.getBody(), new TypeReference<List<StatsDto>>() {});

        if (statsDtos.size() != 0) {
            if (statsDtos.get(0).getHits() == null) {
                return 0L;
            } else {
                return statsDtos.get(0).getHits();
            }
        } else {
            return 0L;
        }
    }

    @Override
    public EventRequestStatusUpdateResult editStatus(Long userId, Long idEvent, EventRequestStatusUpdateRequest statusUpdateRequest) {
        log.info("Получение event {}", statusUpdateRequest);
        Event eventBase = getEvent(idEvent);
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(eventBase.getId());
        if (eventBase.getParticipantLimit() != 0) {
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
        List<Event> events = repositoryEvent.findByInitiator_id(userId, PageRequest.of(from / size, size)).getContent();
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
        if (!event.getInitiator().getId().equals(userId)) {
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
        if (!event.getInitiator().getId().equals(userId)) {
            throw new EwmException("Пользователь не является владельцем события", "User is not owner event", HttpStatus.CONFLICT);
        }
        return serviceParticipantsRequest.getRequestsFromEvent(eventId);
    }

    //ADMIN
    @Override
    public List<EventFullDto> getEventForAdmin(List<Long> users, List<StatusEvent> state, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<EventFullDto> resultEvents = new ArrayList<>();
        List<Event> events = new ArrayList<>();
        List<Long> queryUsers = new ArrayList<>();
        if (users != null) {
            if (users.get(0) != 0L) {
                queryUsers = users;
            }
        }

        List<Long> queryCategories = new ArrayList<>();
        if (categories != null) {
            if (categories.get(0) != 0L) {
                queryCategories = categories;
            }
        }

        if (rangeStart == null && rangeEnd == null) {
            events = repositoryEvent.findEventForAdminWithoutDate(
                            (queryUsers.size() == 0) ? null : queryUsers,
                            state,
                            (queryCategories.size() == 0) ? null : queryCategories,
                            PageRequest.of(from / size, size))
                            .getContent();
        } else {
            events = repositoryEvent.findEventForAdminWithDate(
                            rangeStart,
                            rangeEnd,
                            (queryUsers.size() == 0) ? null : queryUsers,
                            state,
                            (queryCategories.size() == 0) ? null : queryCategories,
                            PageRequest.of(from / size, size))
                            .getContent();
        }
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
        log.info("Получение фильма {}", updateEventAdminRequest);
        Event event = getEvent(id);
        if (updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = serviceCategory.getById(updateEventAdminRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank()) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new EwmException("Недопустимое значения параметров события", "eventDate isBefore min default value", HttpStatus.BAD_REQUEST);
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
        if (updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank()) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        Event refEvent = repositoryEvent.save(event);
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), false);
        return MapperEvent.toEventFullDto(refEvent, cntViews, cntRequest);
    }

    //public
    @Override
    public List<EventShortDto> getEventsPublic(String text,
                                               List<Long> categories,
                                               Boolean paid,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               SortEvent sort,
                                               Integer from,
                                               Integer size,
                                               HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new EwmException("Дата начала больше даты окончания", "Дата начала больше даты окончания", HttpStatus.BAD_REQUEST);
            }
        }
        statsClient.postHit(new HitDto("ewm_service", request.getRequestURI(), request.getRemoteAddr()));
        List<Event> events;
        if (text != null) {
            events = repositoryEvent.findEventForPublicWithText(
                    paid,
                    text,
                    categories,
                    (rangeStart == null) ? LocalDateTime.now().withNano(0) : rangeStart,
                    rangeEnd,
                    PageRequest.of(from, size))
                    .getContent();
        } else {
            events = repositoryEvent.findEventForPublic(
                    paid,
                    categories,
                    (rangeStart == null) ? LocalDateTime.now().withNano(0) : rangeStart,
                    rangeEnd,
                    PageRequest.of(from / size, size))
                    .getContent();
        }

        List<EventShortDto> results = new ArrayList<>();
        if (events.size() != 0) {
            for (Event event : events) {
                Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
                if (onlyAvailable && event.getParticipantLimit() != 0) {
                    if (!event.getParticipantLimit().equals(cntRequest)) {
                        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), true);
                        results.add(MapperEvent.toEventShortDto(event, cntViews, cntRequest));
                    }
                } else {
                    Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), true);
                    results.add(MapperEvent.toEventShortDto(event, cntViews, cntRequest));
                }
            }
            if (sort == SortEvent.VIEWS) {
                Collections.sort(results, new Comparator<EventShortDto>() {
                    @Override
                    public int compare(EventShortDto a1, EventShortDto a2) {
                        return a1.getViews().compareTo(a2.getViews());
                    }
                });
            }
        }
        return results;
    }

    @Override
    public EventFullDto getEventPublicForUserById(Long id, HttpServletRequest request) {
        statsClient.postHit(new HitDto("ewm_service", request.getRequestURI(), request.getRemoteAddr()));
        Event event = repositoryEvent.findById(id).orElseThrow(() -> new EwmException("Событие не найдено", "Event not found", HttpStatus.NOT_FOUND));
        if (event.getState() != StatusEvent.PUBLISHED) {
            throw new EwmException("Событие не найдено", "Event not found", HttpStatus.NOT_FOUND);
        }
        Long cntRequest = serviceParticipantsRequest.countRequestEventConfirmed(event.getId());
        Long cntViews = getCountViews(event.getCreatedOn(), LocalDateTime.now().withNano(0), List.of("/events/" + event.getId()), true);
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
        if (events != null) {
            for (Long eventId : events) {
                resultEvents.add(getEvent(eventId));
            }
        }
        return resultEvents;
    }

}
