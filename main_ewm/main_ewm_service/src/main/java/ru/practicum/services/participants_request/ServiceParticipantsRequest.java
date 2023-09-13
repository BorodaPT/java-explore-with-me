package ru.practicum.services.participants_request;

import events.dto.ParticipationRequestDto;
import events.enum_events.StatusUserRequestEvent;
import ru.practicum.services.participants_request.model.ParticipationRequest;

import java.util.List;

public interface ServiceParticipantsRequest {

    List<ParticipationRequestDto> getRequests(Long userId);

    List<ParticipationRequestDto> getRequestsFromEvent(Long eventId);

    ParticipationRequest getRequest(Long requestId);

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto editUserRequestStatus(Long userId, Long requestId, StatusUserRequestEvent status);

    ParticipationRequestDto editRequestStatus(Long requestId, StatusUserRequestEvent status);

    Long countRequestEventConfirmed(Long idEvent);

    List<ParticipationRequestDto> getRequestsByStatus(Long idEvent, StatusUserRequestEvent status);
}
