package ru.practicum.services.events.model;

import lombok.*;
import ru.practicum.services.compilations.model.Compilation;
import ru.practicum.services.categories.model.Category;
import ru.practicum.services.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(name = "createdOn", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "eventDate", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "publishedOn", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "loc_lat", nullable = false)
    private Float locLat;

    @Column(name = "loc_lon", nullable = false)
    private Float locLon;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participantLimit", nullable = false)
    private Long participantLimit;

    @Column(name = "requestModeration", nullable = false)
    private Boolean requestModeration;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusEvent state;

    @ManyToMany
    @JoinTable(name = "compilations_events",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id"))
    private List<Compilation> compilationList;

    public Event(String title, String annotation, String description, Category category, User initiator,
                 LocalDateTime createdOn, LocalDateTime eventDate, LocalDateTime publishedOn,
                 Float locLat, Float locLon, Boolean paid, Long participantLimit,
                 Boolean requestModeration, StatusEvent state) {
        this.title = title;
        this.annotation = annotation;
        this.description = description;
        this.category = category;
        this.initiator = initiator;
        this.createdOn = createdOn;
        this.eventDate = eventDate;
        this.publishedOn = publishedOn;
        this.locLat = locLat;
        this.locLon = locLon;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.requestModeration = requestModeration;
        this.state = state;
    }


}
