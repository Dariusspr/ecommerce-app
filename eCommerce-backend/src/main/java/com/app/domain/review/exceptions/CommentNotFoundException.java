package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super(ExceptionMessages.COMMENT_NOT_FOUND_MESSAGE);
    }
}
