package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE);
    }
}
