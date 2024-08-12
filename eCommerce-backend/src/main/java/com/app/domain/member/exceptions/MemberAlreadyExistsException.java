package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class MemberAlreadyExistsException extends RuntimeException {
    public MemberAlreadyExistsException() {
        super(ExceptionMessages.MEMBER_ALREADY_EXISTS_MESSAGE);
    }
}
