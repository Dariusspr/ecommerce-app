package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class BadMemberCredentialsException extends RuntimeException{
    public BadMemberCredentialsException() {
        super(ExceptionMessages.MEMBER_BAD_CREDENTIALS_MESSAGE);
    }

}
