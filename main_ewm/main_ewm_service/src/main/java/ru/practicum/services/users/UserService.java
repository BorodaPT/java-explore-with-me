package ru.practicum.services.users;

import ru.practicum.services.users.model.User;
import users.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto user);

    void delete(Long id);

    List<UserDto> get(List<Long> ids, Integer from, Integer size);

    User getById(Long id);
}
