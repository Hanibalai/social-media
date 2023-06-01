package ru.effectivemobile.socialmedia.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String header;
    private String text;
    private byte[] image;

    public static PostDto build(Post post, User user) {
        return new PostDto(post.getId(),
                user.getUsername(),
                post.getHeader(),
                post.getText(),
                post.getImage());
    }
}
