package com.app.domain.item.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException() {
        super("Category was not found.");
    }
}
