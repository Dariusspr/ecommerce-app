package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class VerificationTokenNotFoundException extends RuntimeException {
    public VerificationTokenNotFoundException() {
        super(ExceptionMessages.VERIFICATION_NOT_FOUND_MESSAGE);
    }
}
