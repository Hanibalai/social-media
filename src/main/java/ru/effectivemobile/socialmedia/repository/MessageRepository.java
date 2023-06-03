package ru.effectivemobile.socialmedia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.socialmedia.model.Message;
import ru.effectivemobile.socialmedia.model.User;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
