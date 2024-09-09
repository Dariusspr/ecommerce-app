package com.app.domain.cart.exceptions;

import com.app.global.constants.ExceptionMessages;

public class CartItemNotFoundException extends  RuntimeException{

    public CartItemNotFoundException() {
        super(ExceptionMessages.CART_ITEM_NOT_FOUND_MESSAGE);
    }
}