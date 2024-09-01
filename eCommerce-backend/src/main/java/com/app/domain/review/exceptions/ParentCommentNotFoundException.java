package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ParentCommentNotFoundException extends RuntimeException {
    public ParentCommentNotFoundException() {
        super(ExceptionMessages.PARENT_COMMENT_NOT_FOUND_MESSAGE);
    }
}
