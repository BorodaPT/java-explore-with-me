package compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewCompilationDto {

    @NotNull
    private List<Long> events;

    @NotNull
    private Boolean pinned;

    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
}
