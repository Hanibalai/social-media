package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
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
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;

    public List<PostDto> getUserPosts(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get the list of user's posts: Invalid username");
        }
        List<Post> postList = postRepository.findPostsByUserOrderById(user);
        return postList.stream().map(post -> PostDto.build(post, user)).collect(Collectors.toList());
    }

    public PostDto savePost(String username, Post post) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to save the post: Invalid username");
        }
        post.setUser(user);
        Post createdPost = postRepository.save(post);
        return PostDto.build(createdPost, user);
    }

    public void removePost(String username, long postId) {
        Post post = postRepository.getPostById(postId).orElse(null);
        if (post == null) {
            throw new BadRequestException("Failed to remove the post: Invalid post ID number");
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to remove the post: Invalid username");
        }
        postRepository.delete(post);
    }
}
