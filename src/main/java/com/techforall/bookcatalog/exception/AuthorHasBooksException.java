package com.techforall.bookcatalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class AuthorHasBooksException extends RuntimeException {

    private final Long authorId;

    public AuthorHasBooksException(Long authorId) {
        super(String.format("Cannot delete author with id '%d' because they have associated books", authorId));
        this.authorId = authorId;
    }

    public Long getAuthorId() {
        return authorId;
    }
}

