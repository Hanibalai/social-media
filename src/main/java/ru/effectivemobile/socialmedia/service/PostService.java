package ru.effectivemobile.socialmedia.service;

import lombok.AllArgsConstructor;
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
public class PostService {
    private PostRepository postRepository;
    private UserRepository userRepository;

    public List<PostDto> getUserPosts(String username, int page, int size) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get the list of user's posts: Invalid username");
        }
        List<Post> postList = postRepository.findAllByUserOrderByIdDesc(user, PageRequest.of(page, size));
        return postList.stream().map(PostDto::build).collect(Collectors.toList());
    }

    public List<PostDto> getActivityFeed(String username, int page, int size) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to get user's activity feed: Invalid username");
        }
        List<Post> postList = postRepository.findAllByUserIn(
                user.getSubscriptions(),
                PageRequest.of(page, size, Sort.by("creationTime").descending()));
        return postList.stream().map(PostDto::build).collect(Collectors.toList());
    }

    public PostDto savePost(String username, PostDto postDto) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new BadRequestException("Failed to save the post: Invalid username");
        }
        Post post = new Post();
        post.setUser(user);
        post.setHeader(postDto.getHeader());
        post.setText(postDto.getText());
        post.setImage(postDto.getImage());
        postRepository.save(post);
        return PostDto.build(post);
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
        if (!post.getUser().equals(user)) {
            throw new PostErrorException("Failed to remove the post: The post does not belong to the user");
        }
        postRepository.delete(post);
    }
}
