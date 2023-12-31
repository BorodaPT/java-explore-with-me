package ru.practicum.controller;

import events.dto.EventFullDto;
import events.dto.EventShortDto;
import events.dto.NewEventDto;
import events.dto.ParticipationRequestDto;
import ru.practicum.services.events.enum_events.StatusUserRequestEvent;
import ru.practicum.services.events.model.EventRequestStatusUpdateRequest;
import ru.practicum.services.events.model.EventRequestStatusUpdateResult;
import ru.practicum.services.events.model.UpdateEventUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.events.EventService;
import ru.practicum.services.participants_request.ParticipantsRequestService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Validated
public class UserEventController {

    private final EventService eventService;

    private final ParticipantsRequestService participantsRequestService;

    //event
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable("userId") Long idUser,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        return eventService.createEvent(idUser, newEventDto);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto editEvent(@PathVariable("userId") Long idUser,
                                  @PathVariable("eventId") Long idEvent,
                                  @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return eventService.editEvent(idUser, idEvent, updateEventUserRequest);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult editEventRequest(@PathVariable("userId") Long idUser,
                                                           @PathVariable("eventId") Long idEvent,
                                                           @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.editStatus(idUser, idEvent, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEvent(@PathVariable("userId") Long idUser,
                                        @RequestParam(name = "from", required = false, defaultValue = "0") Integer start,
                                        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        return eventService.getUserEvents(idUser, start, size);

    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventId(@PathVariable("userId") Long idUser,
                                   @PathVariable("eventId") Long idEvent) {
        return eventService.getUserEvent(idUser, idEvent);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventIdRequest(@PathVariable("userId") Long idUser,
                                                           @PathVariable("eventId") Long idEvent) {
        return eventService.getRequestFromEvent(idUser, idEvent);
    }

    //request
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long idUser,
                                                 @RequestParam("eventId") Long eventId) {
        return participantsRequestService.createRequest(idUser, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto editRequest(@PathVariable("userId") Long idUser,
                                               @PathVariable("requestId") Long idRequest) {
        return participantsRequestService.editUserRequestStatus(idUser, idRequest, StatusUserRequestEvent.CANCELED);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequest(@PathVariable("userId") Long idUser) {
        return participantsRequestService.getRequests(idUser);
    }
}
