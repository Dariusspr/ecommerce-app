package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class DuplicateCommentReactionException extends RuntimeException {
    public DuplicateCommentReactionException() {
        super(ExceptionMessages.DUPLICATE_COMMENT_REACTION_MESSAGE);
    }
}
