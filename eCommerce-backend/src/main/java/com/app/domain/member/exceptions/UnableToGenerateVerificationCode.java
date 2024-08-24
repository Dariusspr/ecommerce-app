package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class UnableToGenerateVerificationCode extends RuntimeException {
    public UnableToGenerateVerificationCode() {
        super(ExceptionMessages.UNABLE_TO_GENERATE_VERIFICATION_CODE_MESSAGE);
    }
}
