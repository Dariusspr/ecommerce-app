package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException() {
        super(ExceptionMessages.DUPLICATE_MEMBER_MESSAGE);
    }
}
