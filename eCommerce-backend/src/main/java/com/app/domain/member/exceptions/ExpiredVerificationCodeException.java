package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ExpiredVerificationCodeException extends RuntimeException {
    public ExpiredVerificationCodeException() {
        super(ExceptionMessages.EXPIRED_VERIFICATION_TOKEN_MESSAGE);
    }
}
