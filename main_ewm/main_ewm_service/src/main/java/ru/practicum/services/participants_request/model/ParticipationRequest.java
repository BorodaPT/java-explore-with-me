package ru.practicum.services.participants_request.model;

import events.enum_events.StatusUserRequestEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "participants_request")
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event_id;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusUserRequestEvent status;

    public ParticipationRequest(User requester_id, Event event_id, StatusUserRequestEvent status) {
        this.requester_id = requester_id;
        this.event_id = event_id;
        this.status = status;
        created = LocalDateTime.now().withNano(0);
    }
}
