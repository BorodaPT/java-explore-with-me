package ru.practicum.services.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.users.mapper.MapperUser;
import ru.practicum.services.users.model.User;
import users.dto.UserDto;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServiceUserImpl implements ServiceUser {

    private final RepositoryUser repositoryUser;

    @Transactional
    @Override
    public UserDto create(UserDto user) {
        try {
            return MapperUser.toDTO(repositoryUser.save(MapperUser.toUser(user)));
        } catch (Exception e) {
            throw new EwmException("Недопустимое значения параметров пользователя", e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = repositoryUser.findById(id).orElseThrow(() -> new EwmException("Пользователь для удаления не найден", "User not found", HttpStatus.NOT_FOUND));
        repositoryUser.deleteById(id);
    }

    @Override
    public List<UserDto> get(List<Integer> ids, Integer start, Integer size) {
        return MapperUser.toDTO(repositoryUser.findById(ids, PageRequest.of(start, size)).getContent());
    }

    @Override
    public User getById(Long id) {
        return repositoryUser.findById(id).orElseThrow(() -> new EwmException("Пользователь для удаления не найден", "User not found", HttpStatus.NOT_FOUND));
    }
}
