package ru.effectivemobile.socialmedia.exception;

public class InvitationErrorException extends BadRequestException {
    public InvitationErrorException(String message) {
        super(message);
    }
}
