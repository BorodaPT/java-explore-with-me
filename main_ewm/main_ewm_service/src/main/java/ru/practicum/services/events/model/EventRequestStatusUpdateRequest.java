package ru.practicum.services.events.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.services.events.enum_events.StatusUserRequestEvent;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private StatusUserRequestEvent status;
}
