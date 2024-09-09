package com.app.global.exceptions;

import com.app.domain.cart.exceptions.CartItemNotFoundException;
import com.app.domain.cart.exceptions.CartNotFoundException;
import com.app.domain.cart.exceptions.InsufficientStockException;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.member.exceptions.*;
import com.app.domain.review.exceptions.*;
import com.app.global.constants.ExceptionMessages;
import com.app.global.dtos.ExceptionDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends RuntimeException>, HttpStatus> EXCEPTION_STATUS_MAP = new HashMap<>();

    static {
        EXCEPTION_STATUS_MAP.put(CategoryNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(ParentCategoryNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(MemberNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(MemberAlreadyExistsException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(ItemNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(RoleNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(RoleAlreadyExistsException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(BadMemberCredentialsException.class, HttpStatus.UNAUTHORIZED);
        EXCEPTION_STATUS_MAP.put(AccountNotEnabledException.class, HttpStatus.UNAUTHORIZED);
        EXCEPTION_STATUS_MAP.put(ExpiredVerificationCodeException.class, HttpStatus.GONE);
        EXCEPTION_STATUS_MAP.put(InvalidVerificationTokenException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(ForbiddenException.class, HttpStatus.FORBIDDEN);
        EXCEPTION_STATUS_MAP.put(CommentNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(CommentReactionNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(DuplicateReviewException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(DuplicateCommentReactionException.class, HttpStatus.CONFLICT);
        EXCEPTION_STATUS_MAP.put(ParentCommentNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(ReviewNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(InsufficientStockException.class, HttpStatus.BAD_REQUEST);
        EXCEPTION_STATUS_MAP.put(CartItemNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_STATUS_MAP.put(CartNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleException(Exception exception) {
        HttpStatus status = EXCEPTION_STATUS_MAP.getOrDefault(exception.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        return buildResponseEntity(exception.getMessage() == null ?
                        ExceptionMessages.GENERIC_MESSAGE :
                        exception.getMessage(),
                status);
    }

    private ResponseEntity<ExceptionDTO> buildResponseEntity(String message, HttpStatus status) {
        ExceptionDTO exceptionDTO = new ExceptionDTO(message, LocalDateTime.now(), status.value());
        return new ResponseEntity<>(exceptionDTO, status);
    }
}
