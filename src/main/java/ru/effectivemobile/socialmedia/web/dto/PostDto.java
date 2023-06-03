package ru.effectivemobile.socialmedia.web.dto;

import lombok.Getter;
import ru.effectivemobile.socialmedia.model.Post;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String header;
    private String text;
    private byte[] image;

    private PostDto(long id, String username, String header, String text, byte[] image) {
        this.id = id;
        this.username = username;
        this.header = header;
        this.text = text;
        this.image = image;
    }

    public static PostDto build(Post post) {
        return new PostDto(post.getId(),
                post.getUser().getUsername(),
                post.getHeader(),
                post.getText(),
                post.getImage());
    }
}
