package com.app.domain.cart.exceptions;

import com.app.global.constants.ExceptionMessages;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() {
        super(ExceptionMessages.CART_NOT_FOUND_MESSAGE);
    }
}