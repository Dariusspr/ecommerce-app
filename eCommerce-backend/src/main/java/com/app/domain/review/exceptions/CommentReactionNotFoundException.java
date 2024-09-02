package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class CommentReactionNotFoundException extends RuntimeException {
    public CommentReactionNotFoundException() {
        super(ExceptionMessages.COMMENT_REACTION_NOT_FOUND_MESSAGE);
    }
}
