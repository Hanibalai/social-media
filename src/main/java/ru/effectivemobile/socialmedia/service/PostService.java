package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.effectivemobile.socialmedia.dto.PostDto;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;

    public List<PostDto> getPostsOfUser(String username) {
        List<PostDto> postDtoList = new ArrayList<>();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            List<Post> postList = postRepository.findPostByUserOrderById(user);
            for (Post post : postList) {
                postDtoList.add(pack(post, user));
            }
        } else {
            System.out.println("User not found");
        }
        return postDtoList;
    }

    public PostDto savePost(String username, Post post) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            post.setUser(user);
            Post createdPost = postRepository.save(post);
            return pack(createdPost, user);
        }
        System.out.println("User not found");
        return null;
    }

    public boolean deletePost(String username, long postId) {
        Post post = postRepository.getPostById(postId).orElse(null);
        User user = userRepository.findByUsername(username).orElse(null);
        if (post != null && user != null && post.getUser() == user) {
            postRepository.delete(post);
            return true;
        }
        System.out.println("Error: " + post + "\n" + user);
        return false;
    }

    private static PostDto pack(Post post, User user) {
        return new PostDto(post.getId(),
                user.getUsername(),
                post.getHeader(),
                post.getText(),
                post.getImage());
    }
}
