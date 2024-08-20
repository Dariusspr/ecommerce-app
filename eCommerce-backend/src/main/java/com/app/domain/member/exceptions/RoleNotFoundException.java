package com.app.domain.member.exceptions;

import com.app.global.constants.ExceptionMessages;

public class RoleNotFoundException extends  RuntimeException{
    public RoleNotFoundException() {
        super(ExceptionMessages.ROLE_NOT_FOUND_MESSAGE);
    }

}
