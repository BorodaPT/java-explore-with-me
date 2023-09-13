package ru.practicum.services.participants_request.mapper;

import events.dto.ParticipationRequestDto;
import ru.practicum.services.participants_request.model.ParticipationRequest;

import java.util.ArrayList;
import java.util.List;

public class MapperParticipationRequest {

    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus());
    }

    public static List<ParticipationRequestDto> toDto(Iterable<ParticipationRequest> requests) {
        List<ParticipationRequestDto> result = new ArrayList<>();
        if (requests != null) {
            for (ParticipationRequest request : requests) {
                result.add(toDto(request));
            }
        }
        return result;
    }
}
