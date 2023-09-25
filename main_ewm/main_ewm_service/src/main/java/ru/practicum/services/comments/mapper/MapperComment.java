package ru.practicum.services.comments.mapper;

import comment.dto.CommentDto;
import comment.dto.NewCommentDto;
import ru.practicum.services.comments.model.Comment;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.users.mapper.MapperUser;
import ru.practicum.services.users.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MapperComment {

    public static Comment toNewComment(NewCommentDto newCommentDto, User user, Event event) {
        return new Comment(
                newCommentDto.getDescription(),
                user,
                event,
                LocalDateTime.now().withNano(0),
                null,
                true
        );
    }

    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getDescription(),
                MapperUser.toShortDTO(comment.getCommentator()),
                comment.getEvent().getId(),
                comment.getCreated(),
                comment.getModified(),
                comment.getIsVisible());
    }

    public static List<CommentDto> toDto(List<Comment> comments) {
        List<CommentDto> results = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                results.add(toDto(comment));
            }
        }
        return results;
    }

}
