package com.app.domain.item.exceptions;

import com.app.global.constants.ExceptionMessages;

public class DuplicateCategoryException extends RuntimeException {
    public DuplicateCategoryException() {
        super(ExceptionMessages.DUPLICATE_CATEGORY_MESSAGE);
    }
}
