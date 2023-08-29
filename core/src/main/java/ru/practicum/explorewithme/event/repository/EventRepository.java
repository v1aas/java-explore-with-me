package ru.practicum.explorewithme.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.user.model.User;


public interface EventRepository extends JpaRepository<Event, Integer> {
    Page<Event> findByInitiator(User initiator, Pageable pageable);
}
