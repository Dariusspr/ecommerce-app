package com.app.domain.item.exceptions;

import com.app.global.constants.ExceptionMessages;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
        super(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE);
    }
}