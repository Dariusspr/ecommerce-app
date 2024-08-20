package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class RoleAlreadyExistsException extends RuntimeException {
    public RoleAlreadyExistsException() {
        super(ExceptionMessages.ROLE_ALREADY_EXISTS_MESSAGE);
    }

}
