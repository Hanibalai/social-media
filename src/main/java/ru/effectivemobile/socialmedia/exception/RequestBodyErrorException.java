package ru.effectivemobile.socialmedia.exception;

import lombok.Getter;

@Getter
public class RequestBodyErrorException extends Exception {
    private final String message = "There is an error in the request body";
}
