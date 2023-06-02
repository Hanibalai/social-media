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

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;

    @Transactional
    public List<PostDto> getUserPosts(String username) {
        List<PostDto> postDtoList = new ArrayList<>();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get the list of user's posts: Invalid username");
        }
        List<Post> postList = postRepository.findPostByUserOrderById(user);
        for (Post post : postList)
            postDtoList.add(PostDto.build(post, user));
        return postDtoList;
    }

    @Transactional
    public PostDto savePost(String username, Post post) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to save the post: Invalid username");
        }
        post.setUser(user);
        Post createdPost = postRepository.save(post);
        return PostDto.build(createdPost, user);
    }

    @Transactional
    public void deletePost(String username, long postId) {
        Post post = postRepository.getPostById(postId).orElse(null);
        if (post == null) {
            throw new BadRequestException("Failed to delete the post: Invalid post ID number");
        }
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to delete the post: Invalid username");
        }
        postRepository.delete(post);
    }
}
