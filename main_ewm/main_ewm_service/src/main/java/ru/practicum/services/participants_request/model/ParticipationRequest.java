package ru.practicum.services.participants_request.model;

import ru.practicum.services.events.enum_events.StatusUserRequestEvent;
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
    private User requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusUserRequestEvent status;

    public ParticipationRequest(User requester, Event event, StatusUserRequestEvent status) {
        this.requester = requester;
        this.event = event;
        this.status = status;
        created = LocalDateTime.now().withNano(0);
    }
}
