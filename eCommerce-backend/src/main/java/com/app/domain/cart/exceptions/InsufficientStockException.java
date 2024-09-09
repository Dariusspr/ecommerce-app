package com.app.domain.cart.exceptions;

import com.app.global.constants.ExceptionMessages;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException() {
        super(ExceptionMessages.INSUFFICIENT_STOCK_MESSAGE);
    }
}