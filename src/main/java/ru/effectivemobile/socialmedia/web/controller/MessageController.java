package ru.effectivemobile.socialmedia.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.service.MessageService;
import ru.effectivemobile.socialmedia.web.dto.MessageDto;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/messages")
@SecurityRequirement(name = "JWT")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(
        name = "Messaging Controller",
        description = "Performs actions on messages, such as: " +
                "sending, receiving messages, receiving and viewing message history with a specific user"
)
public class MessageController {
    private MessageService messageService;

    @PostMapping("/{sender}/{recipient}/send")
    @Operation(
            summary = "Sends a message",
            description = "Takes two parameters from the path as input: " +
                    "the usernames of the sender and recipient of a message. " +
                    "Receives a MessageDto object from the request body. " +
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

    @GetMapping("/{username}/sent")
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

    @GetMapping("/{username}/received")
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
