package com.app.domain.item.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ParentCategoryNotFoundException extends RuntimeException {
    public ParentCategoryNotFoundException() {
        super(ExceptionMessages.PARENT_CATEGORY_NOT_FOUND_MESSAGE);
    }
}
