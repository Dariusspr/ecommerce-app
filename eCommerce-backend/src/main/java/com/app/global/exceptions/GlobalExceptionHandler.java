package com.app.global.exceptions;

import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.global.dtos.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CategoryNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleCategoryNotFound(CategoryNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ParentCategoryNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleParentCategoryNotFound(ParentCategoryNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }
}
