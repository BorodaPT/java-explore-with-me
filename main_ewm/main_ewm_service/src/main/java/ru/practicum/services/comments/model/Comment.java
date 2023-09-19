package ru.practicum.services.comments.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.services.events.model.Event;
import ru.practicum.services.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commentator_id", nullable = false)
    private User commentator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime modified;

    @Column(name = "visibility", nullable = false)
    private Boolean isVisible;

    public Comment(String description, User commentator, Event event, LocalDateTime created, LocalDateTime modified, Boolean isVisible) {
        this.description = description;
        this.commentator = commentator;
        this.event = event;
        this.created = created;
        this.modified = modified;
        this.isVisible = isVisible;
    }
}
