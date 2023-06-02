package ru.effectivemobile.socialmedia.exception;

import lombok.Getter;

public class InvitationException extends BadRequestException {
    public InvitationException(String message) {
        super(message);
    }
}
