package events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import events.enum_events.StatusAction;
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
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationEvent locationEvent;

    @JsonProperty(value = "paid")
    private Boolean paid;

    private Long participantLimit;

    @JsonProperty(value = "requestModeration")
    private Boolean requestModeration;

    private StatusAction stateAction;

    @Size(min = 3, max = 120)
    private String title;

}
