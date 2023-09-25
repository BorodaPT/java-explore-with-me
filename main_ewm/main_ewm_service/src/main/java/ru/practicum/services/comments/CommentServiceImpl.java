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
import ru.practicum.services.events.EventService;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.events.model.StatusEvent;
import ru.practicum.services.users.UserService;
import ru.practicum.services.users.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserService userService;

    private final EventService eventService;

    @Transactional
    @Override
    public CommentDto create(Long idUser, Long idEvent, NewCommentDto newCommentDto) {

        User user = userService.getById(idUser);

        Event event = eventService.getEvent(idEvent);

        if (event.getState() == StatusEvent.PENDING) {
            throw new EwmException("Событие не опубликовано", "Event not published or canceled", HttpStatus.BAD_REQUEST);
        }

        Comment comment = MapperComment.toNewComment(newCommentDto, user, event);

        return MapperComment.toDto(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto edit(Long idUser, Long idEvent, Long idComm, NewCommentDto newCommentDto, Boolean isAdmin) {

        Comment comment = commentRepository.findById(idComm).orElseThrow(() -> new EwmException("Комментарий не найден", "Comment not found", HttpStatus.NOT_FOUND));

        if (!isAdmin) {
            if (!comment.getCommentator().getId().equals(idUser)) {
                throw new EwmException("Пользователь не владелец комментария", "user not owner", HttpStatus.NOT_FOUND);
            }
            User user = userService.getById(idUser);
        }

        Event event = eventService.getEvent(idEvent);

        if (newCommentDto.getDescription() != null) {
            comment.setDescription(newCommentDto.getDescription());
            comment.setModified(LocalDateTime.now().withNano(0));
        }

        return MapperComment.toDto(commentRepository.save(comment));
    }

    @Transactional
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

        Event event = eventService.getEvent(idEvent);
        if (commIds.get(0) == 0L) {
            commentRepository.updateVisible(idEvent, null, isVisible);
        } else {
            commentRepository.updateVisible(idEvent, commIds, isVisible);
        }

        return getForEvent(idEvent, null, 0, 20);

    }

    @Transactional
    @Override
    public List<CommentDto> deleteUser(Long idUser, Long idEvent, Long idComm) {
        Comment comment = commentRepository.findById(idComm).orElseThrow(() -> new EwmException("Комментарий не найден", "Comment not found", HttpStatus.NOT_FOUND));

        User user = userService.getById(idUser);

        Event event = eventService.getEvent(idEvent);

        if (!comment.getCommentator().getId().equals(idUser)) {
            throw new EwmException("Пользователь не владелец комментария", "user not owner", HttpStatus.NOT_FOUND);
        }

        commentRepository.deleteById(idComm);

        List<Comment> comments = commentRepository.findAllByEventId(idEvent, true, PageRequest.of(0, 20));

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

        Event event = eventService.getEvent(idEvent);

        if (commIds.get(0) == 0L) {
            commentRepository.deleteAllByEventId(idEvent,null);
        } else {
            commentRepository.deleteAllByEventId(idEvent,commIds);
        }

        List<Comment> comments = commentRepository.findAllByEventId(idEvent, null, PageRequest.of(0, 20));

        return MapperComment.toDto(comments);
    }

    @Override
    public List<CommentDto> getForEvent(Long eventId, Boolean isVisibleComment, Integer from, Integer size) {

        Event event = eventService.getEvent(eventId);

        List<Comment> comments = commentRepository.findAllByEventId(eventId, isVisibleComment, PageRequest.of(from / size, size));

        return MapperComment.toDto(comments);
    }

}
