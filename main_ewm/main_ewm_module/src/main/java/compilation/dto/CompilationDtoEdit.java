package compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompilationDtoEdit {
    private List<Long> events;

    private Boolean pinned;

    @Size(max = 50)
    private String title;
}
