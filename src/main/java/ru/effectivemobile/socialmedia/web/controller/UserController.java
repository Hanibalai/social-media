package ru.effectivemobile.socialmedia.web.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.model.Message;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.service.MessageService;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.service.UserService;
import ru.effectivemobile.socialmedia.web.dto.MessageDto;
import ru.effectivemobile.socialmedia.web.dto.PostDto;
import ru.effectivemobile.socialmedia.web.dto.UserDto;
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
    private MessageService messageService;

    @GetMapping("/{username}")
    public ResponseEntity<?> myPage(@PathVariable("username") String username) {
        return getPosts(username);
    }

    @GetMapping("/visit/{username}")
    public ResponseEntity<?> visitPage(@PathVariable("username") String visitedUsername) {
        return getPosts(visitedUsername);
    }

    private ResponseEntity<?> getPosts(String username) {
        try {
            List<PostDto> userPosts = postService.getUserPosts(username);
            return ResponseEntity.ok(userPosts);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @PostMapping("/{username}/posts/save")
    public ResponseEntity<?> savePost(@PathVariable("username") String username, @RequestBody Post post) {
        try {
            PostDto createdPost = postService.savePost(username, post);
            return ResponseEntity.ok(createdPost);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/posts/{id}/remove")
    public ResponseEntity<?> removePost(@PathVariable("username") String username,
                                        @PathVariable("id") long postId) {
        try {
            postService.removePost(username, postId);
            return ResponseEntity.ok(new MessageResponse("Post was successfully removed"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{recipient}/invitations/{sender}/accept")
    public ResponseEntity<?> acceptInvite(@PathVariable String recipient,
                                          @PathVariable String sender) {
        try {
            userService.acceptInvite(recipient, sender);
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully accepted"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/friends")
    public ResponseEntity<?> getFriends(@PathVariable String username) {
        try {
            List <UserDto> userFriends = userService.getUserFriends(username);
            return ResponseEntity.ok(userFriends);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/friends/{friendUsername}/remove")
    public ResponseEntity<?> removeFriend(@PathVariable String username,
                                          @PathVariable String friendUsername) {
        try {
            userService.removeFriend(username, friendUsername);
            return ResponseEntity.ok(new MessageResponse("Friend was successfully removed"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @PostMapping("/{sender}/friends/{recipient}/sendmessage")
    public ResponseEntity<?> sendMessage(@PathVariable String sender,
                                         @PathVariable String recipient,
                                         @RequestBody Message message) {
        try {
            MessageDto sentMessage = messageService.sendMessage(sender, recipient, message);
            return ResponseEntity.ok(sentMessage);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/messages/sent")
    public ResponseEntity<?> getSentMessages(@PathVariable String username) {
        try {
            List<MessageDto> sentMessages = messageService.getSentMessages(username);
            return ResponseEntity.ok(sentMessages);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/messages/received")
    public ResponseEntity<?> getReceivedMessages(@PathVariable String username) {
        try {
            List<MessageDto> sentMessages = messageService.getReceivedMessages(username);
            return ResponseEntity.ok(sentMessages);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }
}
