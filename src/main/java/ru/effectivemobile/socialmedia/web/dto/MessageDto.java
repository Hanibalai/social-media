package ru.effectivemobile.socialmedia.web.dto;

import lombok.Getter;
import ru.effectivemobile.socialmedia.model.Message;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class MessageDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String sender;
    private final String recipient;
    private final String text;

    private MessageDto(String sender, String recipient, String text) {
        this.sender = sender;
        this.recipient = recipient;
        this.text = text;
    }

    public static MessageDto build(Message message) {
        return new MessageDto(
                message.getSender().getUsername(),
                message.getRecipient().getUsername(),
                message.getText());
    }
}
