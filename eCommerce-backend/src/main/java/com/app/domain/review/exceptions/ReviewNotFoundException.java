package com.app.domain.review.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super(ExceptionMessages.REVIEW_NOT_FOUND_MESSAGE);
    }
}
