package ru.effectivemobile.socialmedia.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.dto.PostDto;
import ru.effectivemobile.socialmedia.dto.response.MessageResponse;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.repository.PostRepository;
import ru.effectivemobile.socialmedia.repository.UserRepository;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.service.UserService;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private PostService postService;
    private UserRepository userRepository;
    private PostRepository postRepository;

    @GetMapping("/{username}")
    public ResponseEntity<List<PostDto>> myPage(@PathVariable("username") String username) {
        List<PostDto> userPosts = postService.getPostsOfUser(username);
        return ResponseEntity.ok(userPosts);
    }

    @PostMapping("/{username}/addpost")
    public ResponseEntity<?> addPost(@PathVariable("username") String username, @RequestBody Post post) {
        PostDto createdPost = postService.savePost(username, post);
        return ResponseEntity.ok(createdPost);
    }

    @GetMapping("/{username}/deletepost/{id}")
    public ResponseEntity<?> deletePost(@PathVariable("username") String username,
                                        @PathVariable("id") long postId) {
        if (postService.deletePost(username, postId)) {
            return ResponseEntity.ok(new MessageResponse("Post was successfully deleted"));
        }
        return ResponseEntity.badRequest().body(new MessageResponse("There is some error in request"));
    }

    @GetMapping("/visit/{username}")
    public ResponseEntity<List<PostDto>> visitPage(@PathVariable("username") String visitedUsername) {
        List<PostDto> visitedUserPosts = postService.getPostsOfUser(visitedUsername);
        return ResponseEntity.ok(visitedUserPosts);
    }
}
