package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
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

    public List<PostDto> getUserPosts(String username) {
        List<PostDto> postDtoList = new ArrayList<>();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            List<Post> postList = postRepository.findPostByUserOrderById(user);
            for (Post post : postList)
                postDtoList.add(PostDto.build(post, user));
        } else {
            throw new RuntimeException("There is an error in the request body");
        }
        return postDtoList;
    }

    public PostDto savePost(String username, Post post) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            post.setUser(user);
            Post createdPost = postRepository.save(post);
            return PostDto.build(createdPost, user);
        } else {
            throw new RuntimeException("There is an error in the request body");
        }
    }

    public void deletePost(String username, long postId) {
        Post post = postRepository.getPostById(postId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        if (post != null && user != null && post.getUser() == user) {
            postRepository.delete(post);
        } else {
            throw new RuntimeException("There is an error in the request body");
        }
    }
}
