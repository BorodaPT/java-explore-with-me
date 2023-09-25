package ru.practicum.services.participants_request;

import events.dto.ParticipationRequestDto;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.events.enum_events.StatusUserRequestEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.events.EventRepository;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.participants_request.mapper.MapperParticipationRequest;
import ru.practicum.services.participants_request.model.ParticipationRequest;
import ru.practicum.services.users.UserService;
import ru.practicum.services.users.model.User;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipantsRequestServiceImpl implements ParticipantsRequestService {

    private final ParticipantsRequestRepository participantsRequestRepository;

    private final UserService userService;

    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        User user = userService.getById(userId);
        return MapperParticipationRequest.toDto(participantsRequestRepository.findByRequester_id(userId));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsFromEvent(Long eventId) {
        return MapperParticipationRequest.toDto(participantsRequestRepository.findByEvent_id(eventId));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByStatus(Long idEvent, StatusUserRequestEvent status) {
        return MapperParticipationRequest.toDto(participantsRequestRepository.findByEvent_idAndStatus(idEvent, status.toString()));
    }

    public ParticipationRequestDto getRequestsByEventAndRequestor(Long idEvent, Long requesterId) {
        return MapperParticipationRequest.toDto(participantsRequestRepository.findByEvent_idAndRequester_id(idEvent, requesterId));
    }

    @Override
    public ParticipationRequest getRequest(Long requestId) {
        ParticipationRequest request = participantsRequestRepository.findById(requestId).orElseThrow(() -> new EwmException("Закпрос не найден", "Request not found", HttpStatus.NOT_FOUND));
        return request;
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userService.getById(userId);
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EwmException("Событие не найдено", "Event not found", HttpStatus.NOT_FOUND));

        if (event.getInitiator().getId().equals(userId)) {
            throw new EwmException("Пользователь владелец события", "User owner event", HttpStatus.CONFLICT);
        }

        if (event.getState() != StatusEvent.PUBLISHED) {
            throw new EwmException("Событие не опубликовано", "Event not public", HttpStatus.CONFLICT);
        }

        ParticipationRequest checkPr = participantsRequestRepository.findByEvent_idAndRequester_id(eventId, userId);
        if (checkPr != null) {
            throw new EwmException("Заявка уже была офрмлена", "Limit", HttpStatus.CONFLICT);
        }

        ParticipationRequest participationRequest = new ParticipationRequest(user, event, StatusUserRequestEvent.PENDING);
        if (event.getParticipantLimit() != 0) {
            if (event.getParticipantLimit().equals(countRequestEventConfirmed(eventId))) {
                throw new EwmException("Свободных мест не осталось", "Limit", HttpStatus.CONFLICT);
            }
        } else {
            participationRequest.setStatus(StatusUserRequestEvent.CONFIRMED);
        }

        if (!event.getRequestModeration()) {
            participationRequest.setStatus(StatusUserRequestEvent.CONFIRMED);
        }

        return MapperParticipationRequest.toDto(participantsRequestRepository.save(participationRequest));
    }

    @Transactional
    @Override
    public ParticipationRequestDto editUserRequestStatus(Long userId, Long requestId, StatusUserRequestEvent status) {
        User user = userService.getById(userId);
        ParticipationRequest participationRequest = participantsRequestRepository.findById(requestId).orElseThrow(() -> new EwmException("Закпрос не найден", "Request not found", HttpStatus.NOT_FOUND));
        if (!participationRequest.getRequester().getId().equals(userId)) {
            throw new EwmException("Отмена заявки доступна только пользователю, который ее оформил", "User is not owner", HttpStatus.CONFLICT);
        }
        participationRequest.setStatus(status);
        return MapperParticipationRequest.toDto(participantsRequestRepository.saveAndFlush(participationRequest));
    }

    @Transactional
    @Override
    public ParticipationRequestDto editRequestStatus(Long requestId, StatusUserRequestEvent status) {
        ParticipationRequest participationRequest = participantsRequestRepository.findById(requestId).orElseThrow(() -> new EwmException("Закпрос не найден", "Request not found", HttpStatus.NOT_FOUND));
        participationRequest.setStatus(status);
        return MapperParticipationRequest.toDto(participantsRequestRepository.saveAndFlush(participationRequest));
    }

    @Override
    public Long countRequestEventConfirmed(Long idEvent) {
        return participantsRequestRepository.getCountConfirmedRequests(idEvent, StatusUserRequestEvent.CONFIRMED.toString());
    }
}
