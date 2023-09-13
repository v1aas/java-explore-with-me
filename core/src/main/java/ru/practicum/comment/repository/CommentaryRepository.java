package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.comment.model.Commentary;

public interface CommentaryRepository extends JpaRepository<Commentary, Integer> {
}