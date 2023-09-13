package ru.practicum.services.events.mapper;

import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.enum_events.StatusEvent;
import events.model.LocationEvent;
import ru.practicum.services.categories.mapper.MapperCategory;
import ru.practicum.services.categories.model.Category;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.users.mapper.MapperUser;
import ru.practicum.services.users.model.User;

import java.time.LocalDateTime;

public class MapperEvent {

    public static Event toNewEvent(NewEventDto eventDto, Category category, User user) {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        return new Event(
                eventDto.getTitle(),
                eventDto.getAnnotation(),
                eventDto.getDescription(),
                category,
                user,
                created,
                eventDto.getEventDate(),
                created,
                eventDto.getLocation().getLat(),
                eventDto.getLocation().getLon(),
                eventDto.getPaid(),
                eventDto.getParticipantLimit(),
                eventDto.getRequestModeration(),
                StatusEvent.PENDING
                );
    }

    public static EventFullDto toNewEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                MapperCategory.toDTO(event.getCategory()),
                0L,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                MapperUser.toShortDTO(event.getInitiator()),
                new LocationEvent(event.getLoc_lat(), event.getLoc_lon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                0L);
    }

    public static EventFullDto toEventFullDto(Event event, Long views, Long requests) {
        return new EventFullDto(
                event.getAnnotation(),
                MapperCategory.toDTO(event.getCategory()),
                requests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                MapperUser.toShortDTO(event.getInitiator()),
                new LocationEvent(event.getLoc_lat(), event.getLoc_lon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                views);
    }

    public static EventShortDto toEventShortDto(Event event, Long views, Long requests) {
        return new EventShortDto(
                event.getAnnotation(),
                MapperCategory.toDTO(event.getCategory()),
                requests,
                event.getEventDate(),
                event.getId(),
                MapperUser.toShortDTO(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                views
        );
    }

}
