package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    List<Event> findByCategory(Category category);

    Page<Event> findByInitiator(User initiator, Pageable pageable);

    Optional<Event> findByInitiatorAndId(User initiator, Integer id);

    @Query("SELECT e " +
            "FROM Event as e " +
            "WHERE (e.annotation LIKE :text OR e.description LIKE :text) " +
            "AND e.category IN (:categories) " +
            "AND e.paid IN (:paid) " +
            "AND e.publishedOn IS NOT NULL " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findByAllQueryNoOnlyAvailable(String text, List<Category> categories, List<Boolean> paid,
                                              LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("SELECT e " +
            "FROM Event as e " +
            "WHERE (e.annotation LIKE %:text% OR e.description LIKE %:text%) " +
            "AND e.category IN (:categories) " +
            "AND e.paid IN (:paid) " +
            "AND e.publishedOn IS NOT NULL " +
            "AND e.confirmedRequests < e.participantLimit " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findByAllQueryOnlyAvailable(String text, List<Category> categories, List<Boolean> paid,
                                            LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("SELECT e " +
            "FROM Event as e " +
            "WHERE e.category IN (:categories) " +
            "AND e.paid IN (:paid) " +
            "AND e.publishedOn IS NOT NULL " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findByNoTextQueryNoOnlyAvailable(List<Category> categories, List<Boolean> paid, LocalDateTime start,
                                                 LocalDateTime end, Pageable page);

    @Query("SELECT e " +
            "FROM Event as e " +
            "WHERE e.category IN (:categories) " +
            "AND e.paid IN (:paid) " +
            "AND e.publishedOn IS NOT NULL " +
            "AND e.confirmedRequests < e.participantLimit " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findByNoTextQueryOnlyAvailable(List<Category> categories, List<Boolean> paid, LocalDateTime start,
                                               LocalDateTime end, Pageable page);

    @Query("SELECT e " +
            "FROM Event as e " +
            "WHERE e.initiator IN (:users) " +
            "AND e.state IN (:states) " +
            "AND e.category IN (:categories) " +
            "AND e.eventDate BETWEEN :start AND :end")
    Page<Event> findByAdminQuery(List<User> users, List<State> states, List<Category> categories,
                                 LocalDateTime start, LocalDateTime end, Pageable page);
}