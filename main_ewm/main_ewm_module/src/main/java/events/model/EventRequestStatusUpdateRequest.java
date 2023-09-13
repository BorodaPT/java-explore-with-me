package events.model;

import events.enum_events.StatusUserRequestEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;

    private StatusUserRequestEvent status;
}
