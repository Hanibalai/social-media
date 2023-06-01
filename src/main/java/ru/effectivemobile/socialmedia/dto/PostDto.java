package ru.effectivemobile.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
}
