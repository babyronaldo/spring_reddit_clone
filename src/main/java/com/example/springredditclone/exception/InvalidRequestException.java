package com.example.springredditclone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends SpringRedditException {
    public InvalidRequestException(String message) {
        super(message);
    }

    public InvalidRequestException() {
        super("Invalid request");
    }

    public InvalidRequestException(String message, Throwable t) {
        super(message, t);
    }
}
