package ru.effectivemobile.socialmedia.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import ru.effectivemobile.socialmedia.model.Post;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Schema(description = "Post entity")
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private final long id;
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private final String username;
    private final String header;
    private final String text;
    private final byte[] image;
    private final Date creationTime;



    private PostDto(long id, String username, String header, String text, byte[] image, Date creationTime) {
        this.id = id;
        this.username = username;
        this.header = header;
        this.text = text;
        this.image = image;
        this.creationTime = creationTime;
    }

    public static PostDto build(Post post) {
        return new PostDto(post.getId(),
                post.getUser().getUsername(),
                post.getHeader(),
                post.getText(),
                post.getImage(),
                post.getCreationTime());
    }
}
