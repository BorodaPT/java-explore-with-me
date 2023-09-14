package ru.practicum.services.users;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.services.users.model.User;
import users.dto.UserDto;

import java.util.List;

@Transactional(readOnly = true)
public interface ServiceUser {

    @Transactional
    UserDto create(UserDto user);

    @Transactional
    void delete(Long id);

    List<UserDto> get(List<Long> ids, Integer start, Integer size);

    User getById(Long id);
}
