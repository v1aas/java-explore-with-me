package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.category.model.Category;
import ru.practicum.comment.model.Commentary;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EVENTS")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "annotation", nullable = false)
    private String annotation;

    @OneToOne
    @JoinColumn(name = "category", nullable = false)
    private Category category;

    @Column(name = "confirmed_request")
    private Integer confirmedRequests;

    @Column(name = "created")
    private LocalDateTime createdOn;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "lat")
    private float lat;

    @Column(name = "lon")
    private float lon;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "participant_limit")
    private Integer participantLimit;

    @Column(name = "published")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private boolean requestModeration;

    @Column(name = "state")
    private State state;

    @Column(name = "title")
    private String title;

    @Column(name = "views")
    private Integer views;

    @OneToOne
    @JoinColumn(name = "initiator", nullable = false)
    private User initiator;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_comments",
            joinColumns = @JoinColumn(name = "event_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "comment_id", nullable = false)
    )
    private List<Commentary> comments;
}