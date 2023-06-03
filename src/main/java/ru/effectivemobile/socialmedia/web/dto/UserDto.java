package ru.effectivemobile.socialmedia.web.dto;

import lombok.Getter;
import ru.effectivemobile.socialmedia.model.User;

import java.io.Serial;
import java.io.Serializable;


@Getter
public class UserDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final long id;
    private final String username;
    private final String email;

    public UserDto(long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public static UserDto build(User user) {
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getEmail());
    }
}
