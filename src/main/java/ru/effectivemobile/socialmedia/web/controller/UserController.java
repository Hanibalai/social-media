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
            summary = "Shows the list of user posts",
            description = "Takes the username from path as input. " +
                    "It can also accept page pagination parameters: " +
                    "the number of pages and the number of posts per page. Returns a list of all user posts"
    )
    public ResponseEntity<?> getUserPosts(
            @PathVariable("username") String username,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "page",
                    defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of displayed posts per page") @RequestParam(value = "size",
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
            summary = "Adds and saves a new post",
            description = "Takes as input a username from the path  " +
                    "and a PostDTO object in JSON format from the request body. " +
                    "Converts an object and saves it to the database. Returns the saved object as a response."
    )
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
            summary = "Removes a user's post by ID",
            description = "Takes the username and post ID from the path as input. " +
                    "Validates the data and removes the post from the database. " +
                    "Returns the response in String format."
    )
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
            summary = "Shows a list of all posts in the user's subscriptions",
            description = "Takes the username from path as input. " +
                    "It can also accept page pagination parameters: " +
                    "the number of pages and the number of posts per page. " +
                    "Returns a list of posts sorted by newest and with pagination settings applied"
    )
    public ResponseEntity<?> activityFeed(
            @PathVariable String username,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "page",
                    defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of displayed posts per page") @RequestParam(value = "size",
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
            summary = "Sends a friend invite",
            description = "Takes two parameters from the path as input: " +
                    "the usernames of the sender and recipient of the invitation. " +
                    "Saves the invitation to the database, subscribes the sender to the recipient. " +
                    "Returns the response in String format."
    )
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
            summary = "Accepts a friend invite",
            description = "Takes two parameters from the path as input: " +
                    "the usernames of the sender and recipient of the invitation. " +
                    "Subscribes the recipient to the sender, saves both users as friends. " +
                    "Removes the invitation from the database. Returns the response in String format."
    )
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
            summary = "Shows the user's friends list",
            description = "Takes the username from the path as input. " +
                    "Validates data. Returns a list of all user's friends"
    )
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
            summary = "Removes user from friends",
            description = "Takes two parameters as input: " +
                    "the username and the name of the user-friend to remove. " +
                    "Unsubscribes a user from a deleted friend, removes a friend. " +
                    "Returns the response in String format."
    )
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
            summary = "Sends a message",
            description = "Takes two parameters from the path as input: " +
                    "the usernames of the sender and recipient of a message. " +
                    "Receives a MessageDto object in JSON format from the request body. " +
                    "Saves the message to the database. Returns the stored message as a response."
    )
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
            summary = "Shows a list of messages sent by the user",
            description = "Takes the username from the path as input. " +
                    "Takes all messages sent by user from the database and returns them as a list."
    )
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
            summary = "Shows a list of messages received by the user",
            description = "Takes the username from the path as input. " +
                    "Takes all messages received by user from the database and returns them as a list.")
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
