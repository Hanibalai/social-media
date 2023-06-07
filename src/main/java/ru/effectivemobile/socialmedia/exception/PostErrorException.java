package ru.effectivemobile.socialmedia.exception;

public class PostErrorException extends BadRequestException {

    public PostErrorException(String message) {
        super(message);
    }
}
