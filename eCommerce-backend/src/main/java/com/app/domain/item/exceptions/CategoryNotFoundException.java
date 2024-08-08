package com.app.domain.item.exceptions;

import com.app.global.constants.ExceptionMessages;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException() {
        super(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE);
    }
}
