package com.example.springredditclone.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NO_CONTENT)
public class NoResultException extends SpringRedditException {
    public NoResultException() {
        super("No results");
    }

    public NoResultException(Throwable t) {
        super("No results", t);
    }
}
