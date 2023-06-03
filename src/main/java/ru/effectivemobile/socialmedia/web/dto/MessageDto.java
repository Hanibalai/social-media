package ru.effectivemobile.socialmedia.web.dto;

import lombok.Getter;
import ru.effectivemobile.socialmedia.model.Post;
import ru.effectivemobile.socialmedia.model.User;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class MessageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String sender;
    private String recipient;
    private String text;

    private MessageDto(String sender, String recipient, String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
    }

    public static MessageDto build(String sender, String recipient, String text) {
        return new MessageDto(sender, recipient, text);
    }
}
