package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class InvalidVerificationTokenException extends RuntimeException {
    public InvalidVerificationTokenException() {
        super(ExceptionMessages.INVALID_VERIFICATION_TOKEN_MESSAGE);
    }

}
