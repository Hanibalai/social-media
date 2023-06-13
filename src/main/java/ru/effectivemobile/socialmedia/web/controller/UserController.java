package ru.effectivemobile.socialmedia.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.service.UserService;
import ru.effectivemobile.socialmedia.web.dto.UserDto;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("api/user")
@SecurityRequirement(name = "JWT")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(
        name = "User Action Controller",
        description = "Performs user actions for interaction with other users, such as: " +
        "invitation to friends, acceptance of friend штмшефешщты, removal from friends, viewing the list of friends"
)
public class UserController {
    private UserService userService;

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
        log.info("Get request from user: {} to invite a new friend: {}", sender, recipient);
        try {
            userService.invite(sender, recipient);
            log.info("Invitation sent successfully");
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully sent"));
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
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
        log.info("New request from user: {} to accept friend invite from user: {}", recipient, sender);
        try {
            userService.acceptInvite(recipient, sender);
            log.info("Invitation accepted successfully");
            return ResponseEntity.ok(new MessageResponse("Friend invitation was successfully accepted"));
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
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
        log.info("New request from user: {} to get the list of friends", username);
        try {
            List <UserDto> userFriends = userService.getUserFriends(username);
            log.info("Friends list received successfully");
            return ResponseEntity.ok(userFriends);
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @DeleteMapping("/{username}/{friendUsername}/remove")
    @Operation(
            summary = "Removes user from friends",
            description = "Takes two parameters as input: " +
                    "the username and the name of the user-friend to remove. " +
                    "Unsubscribes a user from a deleted friend, removes a friend. " +
                    "Returns the response in String format."
    )
    public ResponseEntity<?> removeFriend(@PathVariable String username,
                                          @PathVariable String friendUsername) {
        log.info("New request from user: {} to remove user: {} from friends list", username, friendUsername);
        try {
            userService.removeFriend(username, friendUsername);
            log.info("Friend successfully removed");
            return ResponseEntity.ok(new MessageResponse("Friend was successfully removed"));
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }
}
