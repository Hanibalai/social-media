package ru.effectivemobile.socialmedia.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.service.MessageService;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.service.UserService;
import ru.effectivemobile.socialmedia.web.dto.MessageDto;
import ru.effectivemobile.socialmedia.web.dto.PostDto;
import ru.effectivemobile.socialmedia.web.dto.UserDto;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@Tag(name = "User action controller", description = "Performs all user actions after login")
@SecurityRequirement(name = "JWT")
public class UserController {
    private UserService userService;
    private PostService postService;
    private MessageService messageService;

    @GetMapping("/{username}")
    @Operation(
            summary = "User posts",
            description = "Shows all user posts with pagination settings"
    )
    public ResponseEntity<?> getUserPosts(
            @PathVariable("username") String username,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "page",
                    defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "size",
                    defaultValue = "10") @Min(1) @Max(100) int size) {

        try {
            List<PostDto> userPosts = postService.getUserPosts(username, page, size);
            return ResponseEntity.ok(userPosts);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @PostMapping("/{username}/posts/save")
    @Operation(
            summary = "Saving a post",
            description = "Allows the user to add and save a new post")
    public ResponseEntity<?> savePost(@PathVariable("username") String username,
                                      @RequestBody PostDto post) {

        try {
            PostDto createdPost = postService.savePost(username, post);
            return ResponseEntity.ok(createdPost);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @DeleteMapping("/{username}/posts/{id}/remove")
    @Operation(
            summary = "Removing a post",
            description = "Allows the user to remove his post by ID number")
    public ResponseEntity<?> removePost(
            @PathVariable("username") String username,
            @Parameter(description = "Post ID number") @PathVariable("id") long postId) {

        try {
            postService.removePost(username, postId);
            return ResponseEntity.ok(new MessageResponse("Post was successfully removed"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{username}/activityfeed")
    @Operation(
            summary = "User activity feed",
            description = "Shows all subscription posts with pagination settings and sorted by newest")
    public ResponseEntity<?> activityFeed(
            @PathVariable String username,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "page",
                    defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "size",
                    defaultValue = "10") @Min(1) @Max(100) int size) {

        try {
            List<PostDto> activityFeed = postService.getActivityFeed(username, page, size);
            return ResponseEntity.ok(activityFeed);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{sender}/{recipient}/invite")
    @Operation(
            summary = "Sending a friend invitation",
            description = "Allows the user to invite a new friend")
    public ResponseEntity<?> invite(
            @Parameter(description = "Invitation sender") @PathVariable String sender,
            @Parameter(description = "Recipient of the invitation") @PathVariable String recipient) {
        try {
            userService.invite(sender, recipient);
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully sent"));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @GetMapping("/{recipient}/{sender}/accept")
    @Operation(
            summary = "Friend invitation accepting",
            description = "Allows the user to accept a friend invite from another user")
    public ResponseEntity<?> acceptInvite(
            @Parameter(description = "Recipient of the invitation") @PathVariable String recipient,
            @Parameter(description = "Invitation sender") @PathVariable String sender) {
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
    @Operation(
            summary = "User friends list",
            description = "Shows a list of all user friends")
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

    @DeleteMapping("/{username}/friends/{friendUsername}/remove")
    @Operation(
            summary = "Removing user from friends",
            description = "Allows the user to remove a friend")
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
    @Operation(
            summary = "Sending a message",
            description = "Allows the user to send a message to his friend")
    public ResponseEntity<?> sendMessage(
            @Parameter(description = "Message sender") @PathVariable String sender,
            @Parameter(description = "Message recipient") @PathVariable String recipient,
            @Parameter(description = "Message to be sent") @RequestBody MessageDto message) {
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
    @Operation(
            summary = "User sent messages",
            description = "Shows a list of messages sent by the user")
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
    @Operation(
            summary = "User received messages",
            description = "Shows a list of messages received by the user")
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
