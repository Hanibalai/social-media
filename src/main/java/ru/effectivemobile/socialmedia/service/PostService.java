package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.exception.PostErrorException;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.web.dto.PostDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;

    public List<PostDto> getUserPosts(String username, int page, int size) {
        log.debug("Database query to get a list of posts of the user: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to get the list of user's posts: Invalid username"));
        List<Post> postList = postRepository.findAllByUserOrderByIdDesc(user, PageRequest.of(page, size));
        log.debug("Retrieved successful");
        return postList.stream().map(PostDto::build).collect(Collectors.toList());
    }

    public List<PostDto> getActivityFeed(String username, int page, int size) {
        log.debug("Database query to get activity feed of the user: {}", username);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to get user's activity feed: Invalid username"));
        List<Post> postList = postRepository.findAllByUserIn(
                user.getSubscriptions(),
                PageRequest.of(page, size, Sort.by("creationTime").descending())
        );
        log.debug("Retrieved successful");
        return postList.stream().map(PostDto::build).collect(Collectors.toList());
    }

    public PostDto savePost(String username, PostDto postDto) {
        log.debug("Database query to save a new post: username-{}, post-{}", username, postDto);
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to save the post: Invalid username"));
        Post post = new Post();
        post.setUser(user);
        post.setHeader(postDto.getHeader());
        post.setText(postDto.getText());
        post.setImage(postDto.getImage());
        postRepository.save(post);
        log.debug("New post has been saved to the database: {}", post);
        return PostDto.build(post);
    }

    public void removePost(String username, long postId) {
        log.debug("Database query to remove the post: username-{}, post ID-{}", username, postId);
        Post post = postRepository.getPostById(postId).orElseThrow(
                () -> new BadRequestException("Failed to remove the post: Invalid post ID number"));
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new BadRequestException("Failed to remove the post: Invalid username"));
        if (!post.getUser().equals(user)) {
            throw new PostErrorException("Failed to remove the post: The post does not belong to the user");
        }
        postRepository.delete(post);
        log.debug("Post has been removed from the database");
    }
}
