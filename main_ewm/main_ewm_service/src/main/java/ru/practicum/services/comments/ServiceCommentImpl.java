package ru.practicum.services.comments;

import comment.dto.CommentDto;
import comment.dto.NewCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.EwmException;
import ru.practicum.services.comments.mapper.MapperComment;
import ru.practicum.services.comments.model.Comment;
import ru.practicum.services.events.ServiceEvent;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.users.ServiceUser;
import ru.practicum.services.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ServiceCommentImpl implements ServiceComment {

    private final RepositoryComment repositoryComment;

    private final ServiceUser serviceUser;

    private final ServiceEvent serviceEvent;

    @Transactional
    @Override
    public CommentDto create(Long idUser, Long idEvent, NewCommentDto newCommentDto) {

        User user = serviceUser.getById(idUser);

        Event event = serviceEvent.getEvent(idEvent);

        if (event.getState() == StatusEvent.PENDING) {
            throw new EwmException("Событие не опубликовано", "Event not published or canceled", HttpStatus.BAD_REQUEST);
        }

        Comment comment = MapperComment.toNewComment(newCommentDto, user, event);

        return MapperComment.toDto(repositoryComment.save(comment));
    }

    @Transactional
    @Override
    public CommentDto edit(Long idUser, Long idEvent, Long idComm, NewCommentDto newCommentDto, Boolean isAdmin) {

        Comment comment = repositoryComment.findById(idComm).orElseThrow(() -> new EwmException("Комментарий не найден", "Comment not found", HttpStatus.NOT_FOUND));

        if (!isAdmin) {
            if (!comment.getCommentator().getId().equals(idUser)) {
                throw new EwmException("Пользователь не владелец комментария", "user not owner", HttpStatus.NOT_FOUND);
            }
            User user = serviceUser.getById(idUser);
        }

        Event event = serviceEvent.getEvent(idEvent);

        if (newCommentDto.getDescription() != null) {
            comment.setDescription(newCommentDto.getDescription());
            comment.setModified(LocalDateTime.now().withNano(0));
        }

        return MapperComment.toDto(repositoryComment.save(comment));
    }

    @Override
    public List<CommentDto> editVisible(Long idEvent, List<Long> commIds, Boolean isVisible) {

        if (isVisible == null) {
            throw new EwmException("Не указан признак видимости", "State visible is empty", HttpStatus.BAD_REQUEST);
        }

        if (commIds == null) {
            throw new EwmException("Не указан список комментариев", "List is empty", HttpStatus.BAD_REQUEST);
        }
        if (commIds.size() == 0) {
            throw new EwmException("Не указан список комментариев", "List is empty", HttpStatus.BAD_REQUEST);
        }

        Event event = serviceEvent.getEvent(idEvent);
        if (commIds.get(0) == 0L) {
            repositoryComment.updateVisible(idEvent, null, isVisible);
        } else {
            repositoryComment.updateVisible(idEvent, commIds, isVisible);
        }

        return getForEvent(idEvent, null, 0, 20);

    }

    @Transactional
    @Override
    public List<CommentDto> deleteUser(Long idUser, Long idEvent, Long idComm) {
        Comment comment = repositoryComment.findById(idComm).orElseThrow(() -> new EwmException("Комментарий не найден", "Comment not found", HttpStatus.NOT_FOUND));

        User user = serviceUser.getById(idUser);

        Event event = serviceEvent.getEvent(idEvent);

        if (!comment.getCommentator().getId().equals(idUser)) {
            throw new EwmException("Пользователь не владелец комментария", "user not owner", HttpStatus.NOT_FOUND);
        }

        repositoryComment.deleteById(idComm);

        List<Comment> comments = repositoryComment.findAllByEventId(idEvent, true, PageRequest.of(0, 20));

        return MapperComment.toDto(comments);

    }

    @Transactional
    @Override
    public List<CommentDto> deleteAdmin(Long idEvent, List<Long> commIds) {
        if (commIds == null) {
            throw new EwmException("Не указан список комментариев", "List is empty", HttpStatus.BAD_REQUEST);
        }
        if (commIds.size() == 0) {
            throw new EwmException("Не указан список комментариев", "List is empty", HttpStatus.BAD_REQUEST);
        }

        Event event = serviceEvent.getEvent(idEvent);

        if (commIds.get(0) == 0L) {
            repositoryComment.deleteAllByEventId(idEvent,null);
        } else {
            repositoryComment.deleteAllByEventId(idEvent,commIds);
        }

        List<Comment> comments = repositoryComment.findAllByEventId(idEvent, null, PageRequest.of(0, 20));

        return MapperComment.toDto(comments);
    }

    @Override
    public List<CommentDto> getForEvent(Long eventId, Boolean isVisibleComment, Integer from, Integer size) {

        Event event = serviceEvent.getEvent(eventId);

        List<Comment> comments = repositoryComment.findAllByEventId(eventId, isVisibleComment, PageRequest.of(from / size, size));

        return MapperComment.toDto(comments);
    }

}
