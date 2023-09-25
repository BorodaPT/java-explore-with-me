package ru.practicum.services.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.comments.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c " +
                   "where c.event.id = :eventId " +
                   "AND c.isVisible = COALESCE(:isVisible, c.isVisible) " +
                   "order by c.created ")
    List<Comment> findAllByEventId(Long eventId, Boolean isVisible, Pageable pageable);

    @Modifying
    @Query(value = "delete from Comment c " +
            "where c.event.id = :eventId " +
            "AND (coalesce(:commIds, null) is null or (c.id in :commIds)) ")
    void deleteAllByEventId(Long eventId, List<Long> commIds);


    @Modifying
    @Query(value = "update Comment set isVisible = :isVisible " +
            "where event.id = :eventId " +
            "AND (coalesce(:commIds, null) is null or (id in :commIds))")
    void updateVisible(Long eventId, List<Long> commIds, Boolean isVisible);
}
