package events.model;

import categories.dto.CategoryDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import events.enum_events.StatusReview;
import events.model.LocationEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    private String annotation;

    private CategoryDto category;

    @Size(min = 20, max = 7000)
    private String description;

    private LocalDateTime eventDate;

    private LocationEvent locationEvent;

    @JsonProperty(value = "paid")
    private Boolean paid;

    private Integer participantLimit;

    @JsonProperty(value = "requestModeration")
    private Boolean requestModeration;

    private StatusReview stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
