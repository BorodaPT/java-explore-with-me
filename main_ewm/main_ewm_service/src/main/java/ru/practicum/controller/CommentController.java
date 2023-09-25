package ru.practicum.controller;

import comment.dto.CommentDto;
import comment.dto.NewCommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.services.comments.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CommentController {

    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users/{userId}/events/{eventId}/comments")
    public CommentDto create(@PathVariable("userId") Long idUser,
                             @PathVariable("eventId") Long eventId,
                             @Valid @RequestBody NewCommentDto commentDto) {
        return commentService.create(idUser, eventId, commentDto);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public CommentDto edit(@PathVariable("userId") Long userId,
                           @PathVariable("eventId") Long eventId,
                           @PathVariable("comId") Long comId,
                           @Valid @RequestBody NewCommentDto commentDto) {
        return commentService.edit(userId, eventId, comId, commentDto, false);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{comId}")
    public List<CommentDto> deleteOwner(@PathVariable("userId") Long userId,
                            @PathVariable("eventId") Long eventId,
                            @PathVariable("comId") Long comId) {
        return commentService.deleteUser(userId, eventId, comId);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentDto> getEventComments(@PathVariable("eventId") Long eventId,
                                             @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                             @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return commentService.getForEvent(eventId, true, from, size);
    }


    //admin
    @GetMapping("/admin/events/{eventId}/comments")
    public List<CommentDto> getEventCommentsAdmin(@PathVariable("eventId") Long eventId,
                                                      @RequestParam(name = "isVisible", required = false) Boolean isVisible,
                                                      @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
                                                      @RequestParam(name = "size", required = false, defaultValue = "10") Integer size) {
        return commentService.getForEvent(eventId, isVisible, from, size);
    }

    @PatchMapping("/admin/events/{eventId}/comments/{comId}")
    public CommentDto editAdmin(@PathVariable("eventId") Long eventId,
                                @PathVariable("comId") Long comId,
                                @Valid @RequestBody NewCommentDto commentDto) {
        return commentService.edit(null, eventId, comId, commentDto, true);
    }

    @PatchMapping("/admin/events/{eventId}/comments/visible")
    public List<CommentDto> editAdminVisible(@PathVariable("eventId") Long eventId,
                                             @RequestParam(name = "isVisible") Boolean isVisible,
                                             @RequestParam(name = "commIds") List<Long> comments) {
        return commentService.editVisible(eventId, comments, isVisible);
    }

    @DeleteMapping("/admin/events/{eventId}/comments")
    public List<CommentDto> deleteCommentsAdmin(@PathVariable("eventId") Long eventId,
                                                @RequestParam(name = "commIds") List<Long> comments) {
       return commentService.deleteAdmin(eventId, comments);
    }
}
