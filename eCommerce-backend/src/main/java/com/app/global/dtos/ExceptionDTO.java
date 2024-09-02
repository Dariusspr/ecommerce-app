package com.app.global.dtos;

import java.time.LocalDateTime;

public record ExceptionDTO(String message, LocalDateTime now, int value) {
}
