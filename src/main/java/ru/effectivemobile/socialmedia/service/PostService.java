package ru.effectivemobile.socialmedia.service;

import jdk.dynalink.linker.LinkerServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    public List<Post> getPostsOfUser(User user) {
        return postRepository.findAllByUserOrderById(user);
    }

    public Post savePost(User user, String header, String text) {
        Post post = new Post();
        post.setUser(user);
        post.setHeader(header);
        post.setText(text);
        return postRepository.save(post);
    }
}
