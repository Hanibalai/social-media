package ru.effectivemobile.socialmedia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/auth/user/{id}")
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    PostService postService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;


    @GetMapping()
    public ResponseEntity<List<Post>> myPage(@PathVariable("id") long id) {
        List<Post> userPosts = postService.getPostsOfUser(userRepository.findUserById(id));
        return ResponseEntity.ok(userPosts);
    }

    @PostMapping("/addpost")
    public ResponseEntity<?> addPost(@PathVariable("id") long id, @RequestBody Post post) {
        User user = userRepository.findUserById(id);
        postService.savePost(user, post.getHeader(), post.getText());
        return ResponseEntity.created(URI.create("/user/id")).body(post);
    }

}
