package events.dto;

import categories.dto.CategoryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import events.enum_events.StatusEvent;
import events.model.LocationEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import users.dto.UserShortDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;

    private LocalDateTime createdOn;

    private String description;

    private LocalDateTime eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationEvent location;

    @JsonProperty(value = "paid")
    private Boolean paid;

    private Long participantLimit;

    private LocalDateTime publishedOn;

    @JsonProperty(value = "requestModeration")
    private Boolean requestModeration;

    private StatusEvent state;

    private String title;

    private Long views;

}
