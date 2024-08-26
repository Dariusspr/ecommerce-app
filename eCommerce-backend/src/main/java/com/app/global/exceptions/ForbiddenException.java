package com.app.global.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super(ExceptionMessages.FORBIDDEN_MESSAGE);
    }
}