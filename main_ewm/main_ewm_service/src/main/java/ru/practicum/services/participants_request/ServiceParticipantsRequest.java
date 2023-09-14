package ru.practicum.services.participants_request;

import events.dto.ParticipationRequestDto;
import events.enum_events.StatusUserRequestEvent;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.services.participants_request.model.ParticipationRequest;

import java.util.List;

@Transactional(readOnly = true)
public interface ServiceParticipantsRequest {

    List<ParticipationRequestDto> getRequests(Long userId);

    List<ParticipationRequestDto> getRequestsFromEvent(Long eventId);

    ParticipationRequest getRequest(Long requestId);

    @Transactional
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    @Transactional
    ParticipationRequestDto editUserRequestStatus(Long userId, Long requestId, StatusUserRequestEvent status);

    @Transactional
    ParticipationRequestDto editRequestStatus(Long requestId, StatusUserRequestEvent status);

    Long countRequestEventConfirmed(Long idEvent);

    List<ParticipationRequestDto> getRequestsByStatus(Long idEvent, StatusUserRequestEvent status);
}
