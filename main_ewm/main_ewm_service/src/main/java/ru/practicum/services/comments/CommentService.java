package ru.practicum.services.comments;

import comment.dto.CommentDto;
import comment.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    CommentDto create(Long idUser, Long idEvent, NewCommentDto newCommentDto);

    CommentDto edit(Long idUser, Long idEvent, Long idComm, NewCommentDto newCommentDto, Boolean isAdmin);

    List<CommentDto> editVisible(Long idEvent, List<Long> commIds, Boolean isVisible);

    List<CommentDto> deleteUser(Long idUser, Long idEvent, Long idComm);

    List<CommentDto> deleteAdmin(Long idEvent, List<Long> commIds);

    List<CommentDto> getForEvent(Long eventId, Boolean isVisibleComment, Integer from, Integer size);


}
