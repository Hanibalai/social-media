package ru.effectivemobile.socialmedia.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.effectivemobile.socialmedia.exception.BadRequestException;
import ru.effectivemobile.socialmedia.service.PostService;
import ru.effectivemobile.socialmedia.web.dto.PostDto;
import ru.effectivemobile.socialmedia.web.dto.response.MessageResponse;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/posts")
@SecurityRequirement(name = "JWT")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(
        name = "Post Action Controller",
        description = "Performs all user actions on posts, such as: getting and viewing the activity feed, " +
                "viewing the posts of a specific user, saving and deleting posts."
)
public class PostController {
    private PostService postService;

    @GetMapping("/{username}")
    @Operation(
            summary = "Shows the list of user posts",
            description = "Takes the username from path as input. " +
                    "It can also accept page pagination parameters: " +
                    "the number of pages and the number of posts per page. Returns a list of all user posts"
    )
    public ResponseEntity<?> getPosts(
            @PathVariable("username") String username,
            @Parameter(description = "Number of displayed pages") @RequestParam(value = "page",
                    defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of displayed posts per page") @RequestParam(value = "size",
                    defaultValue = "10") @Min(1) @Max(100) int size) {
        log.info("New request to get list of user's posts: {}", username);
        try {
            List<PostDto> userPosts = postService.getUserPosts(username, page, size);
            log.info("List of posts received successfully");
            return ResponseEntity.ok(userPosts);
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @PostMapping("/{username}/save")
    @Operation(
            summary = "Adds and saves a new post",
            description = "Takes as input a username from the path and a PostDTO object from the request body. " +
                    "Converts an object and saves it to the database. Returns the saved object as a response."
    )
    public ResponseEntity<?> savePost(@PathVariable("username") String username,
                                      @RequestBody PostDto post) {
        log.info("New request from user: {} to save a new post", username);
        try {
            PostDto createdPost = postService.savePost(username, post);
            log.info("Post successfully saved");
            return ResponseEntity.ok(createdPost);
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }

    @DeleteMapping("/{username}/remove/{id}")
    @Operation(
            summary = "Removes a user's post by ID",
            description = "Takes the username and post ID from the path as input. " +
                    "Validates the data and removes the post from the database. " +
                    "Returns the response in String format."
    )
    public ResponseEntity<?> removePost(
            @PathVariable("username") String username,
            @Parameter(description = "Post ID number") @PathVariable("id") long postId) {
        log.info("New request from user: {} to remove a post, post ID: {}", username, postId);
        try {
            postService.removePost(username, postId);
            log.info("Post successfully removed");
            return ResponseEntity.ok(new MessageResponse("Post was successfully removed"));
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
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
        log.info("New request from user: {} to get his activity feed", username);
        try {
            List<PostDto> activityFeed = postService.getActivityFeed(username, page, size);
            log.info("Activity feed received successfully");
            return ResponseEntity.ok(activityFeed);
        } catch (BadRequestException e) {
            log.warn(e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.internalServerError().body(new MessageResponse("Server error"));
        }
    }
}
