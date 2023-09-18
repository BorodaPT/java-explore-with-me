package ru.practicum.services.users.mapper;

import ru.practicum.services.users.model.User;
import users.dto.UserDto;
import users.dto.UserShortDto;

import java.util.ArrayList;
import java.util.List;

public class MapperUser {

    public static UserDto toDTO(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static UserShortDto toShortDTO(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static User toUser(UserDto userDTO) {
        return new User(userDTO.getId(), userDTO.getName(), userDTO.getEmail());
    }

    public static List<UserDto> toDTO(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        if (users != null) {
            for (User user : users) {
                result.add(toDTO(user));
            }
        }
        return result;
    }
}
