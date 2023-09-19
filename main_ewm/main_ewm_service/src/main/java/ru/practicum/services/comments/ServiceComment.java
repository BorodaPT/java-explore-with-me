package ru.practicum.services.comments;

import comment.dto.CommentDto;
import comment.dto.NewCommentDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ServiceComment {

    @Transactional
    CommentDto create(Long idUser, Long idEvent, NewCommentDto newCommentDto);

    @Transactional
    CommentDto edit(Long idUser, Long idEvent, Long idComm, NewCommentDto newCommentDto, Boolean isAdmin);

    @Transactional
    List<CommentDto> editVisible(Long idEvent, List<Long> commIds, Boolean isVisible);

    @Transactional
    List<CommentDto> deleteUser(Long idUser, Long idEvent, Long idComm);

    @Transactional
    List<CommentDto> deleteAdmin(Long idEvent, List<Long> commIds);

    List<CommentDto> getForEvent(Long eventId, Boolean isVisibleComment, Integer from, Integer size);


}
