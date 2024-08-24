package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class AccountNotEnabledException extends RuntimeException {
    public AccountNotEnabledException() {
        super(ExceptionMessages.ACCOUNT_NOT_ENABLED_MESSAGE);
    }
}
