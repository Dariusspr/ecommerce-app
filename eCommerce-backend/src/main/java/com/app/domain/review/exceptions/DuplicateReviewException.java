package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class DuplicateReviewException extends RuntimeException {
    public DuplicateReviewException() {
        super(ExceptionMessages.DUPLICATE_REVIEW_MESSAGE);
    }
}
