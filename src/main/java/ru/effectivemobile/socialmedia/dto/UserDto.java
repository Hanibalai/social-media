package ru.effectivemobile.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class UserDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private List<PostDto> postList;
}
