package ru.effectivemobile.socialmedia.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.service.UserService;
import ru.effectivemobile.socialmedia.web.dto.PostDto;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class UserController {
    private UserService userService;
    private PostService postService;

    @GetMapping("/{username}")
    public ResponseEntity<?> myPage(@PathVariable("username") String username) {
        try {
            List<PostDto> userPosts = postService.getUserPosts(username);
            return ResponseEntity.ok(userPosts);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/{username}/addpost")
    public ResponseEntity<?> addPost(@PathVariable("username") String username, @RequestBody Post post) {
        try {
            PostDto createdPost = postService.savePost(username, post);
            return ResponseEntity.ok(createdPost);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{username}/{id}/deletepost")
    public ResponseEntity<?> deletePost(@PathVariable("username") String username,
                                        @PathVariable("id") long postId) {
        try {
            postService.deletePost(username, postId);
            return ResponseEntity.ok(new MessageResponse("Post was successfully deleted"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/visit/{username}")
    public ResponseEntity<?> visitPage(@PathVariable("username") String visitedUsername) {
        try {
            List<PostDto> visitedUserPosts = postService.getUserPosts(visitedUsername);
            return ResponseEntity.ok(visitedUserPosts);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{sender}/{recipient}/invite")
    public ResponseEntity<?> invite(@PathVariable String sender,
                                    @PathVariable String recipient) {
        try {
            userService.invite(sender, recipient);
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully sent"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{recipient}/{sender}/accept")
    public ResponseEntity<?> accept(@PathVariable String recipient,
                                    @PathVariable String sender) {
        try {
            userService.acceptInvite(recipient, sender);
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully accepted"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
