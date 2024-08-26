package com.app.global.exceptions;

import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.member.exceptions.*;
import com.app.global.constants.ExceptionMessages;
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

    @ExceptionHandler(MemberNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleMemberNotFound(MemberNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    ResponseEntity<ExceptionDTO> handleMemberAlreadyExists(MemberAlreadyExistsException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleItemNotFound(ItemNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    ResponseEntity<ExceptionDTO> handleRoleNotFound(RoleNotFoundException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    ResponseEntity<ExceptionDTO> handleRoleAlreadyExists(RoleAlreadyExistsException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadMemberCredentialsException.class)
    ResponseEntity<ExceptionDTO> handleBadMemberCredentialsException(BadMemberCredentialsException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccountNotEnabledException.class)
    ResponseEntity<ExceptionDTO> handleAccountNotEnabledException(AccountNotEnabledException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredVerificationCodeException.class)
    ResponseEntity<ExceptionDTO> handleExpiredVerificationCodeException(ExpiredVerificationCodeException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.GONE);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    ResponseEntity<ExceptionDTO> handleInvalidVerificationTokenException(InvalidVerificationTokenException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ForbiddenException.class)
    ResponseEntity<ExceptionDTO> handleForbiddenException(ForbiddenException exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(exception.getMessage());
        return new ResponseEntity<>(exceptionDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionDTO> handleGeneric(Exception exception) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(ExceptionMessages.GENERIC_MESSAGE);
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
