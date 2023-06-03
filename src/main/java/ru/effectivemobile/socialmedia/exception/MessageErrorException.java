package ru.effectivemobile.socialmedia.exception;

public class MessageErrorException extends BadRequestException {
    public MessageErrorException(String message) {
        super(message);
    }
}
