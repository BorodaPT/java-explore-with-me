package ru.practicum.services.participants_request;

import events.enum_events.StatusUserRequestEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.services.participants_request.model.ParticipationRequest;

import java.util.List;

@Repository
public interface RepositoryParticipantsRequest  extends JpaRepository<ParticipationRequest, Long> {

    @Query(value = "SELECT COUNT(pr.*) FROM participants_request as pr WHERE pr.event_id = ?1 AND pr.status = ?2", nativeQuery = true)
    Long getCountConfirmedRequests(Long idEvent, String status);

    @Query(value = "select pr.* from participants_request as pr where requester_id = ?1 ", nativeQuery = true)
    List<ParticipationRequest> findByRequester_id(Long userId);

    @Query(value = "select pr.* from participants_request as pr where event_id = ?1 ", nativeQuery = true)
    List<ParticipationRequest> findByEvent_id(Long eventId);

    @Query(value = "select pr.* from participants_request as pr where pr.event_id = ?1 and pr.status = ?2 ", nativeQuery = true)
    List<ParticipationRequest> findByEvent_idAndStatus(Long eventId, StatusUserRequestEvent status);

    @Query(value = "select pr.* from participants_request as pr where pr.event_id = ?1 and pr.requester_id = ?2 ", nativeQuery = true)
    ParticipationRequest findByEvent_idAndRequester_id(Long idEvent, Long requesterId);

}
