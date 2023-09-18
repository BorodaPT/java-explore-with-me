package users.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;

    @NotBlank
    @Size(min = 2, max = 250, message = "Превышена максимальная длина комментария(200)")
    private String name;

    @NotBlank
    @Email(message = "Неверный формат электронной почты")
    @Size(min = 6, max = 254, message = "Превышена максимальная длина комментария(200)")
    private String email;

    public UserDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
