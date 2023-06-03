package ru.effectivemobile.socialmedia.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByUserOrderByIdDesc(User user, PageRequest pageRequest);

    List<Post> findAllByUserIn(List<User> subscribes, PageRequest pageRequest);

    Optional<Post> getPostById(long id);
}
