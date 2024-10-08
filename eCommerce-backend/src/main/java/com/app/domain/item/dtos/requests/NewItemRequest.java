package com.app.domain.item.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public record NewItemRequest(

        @NotBlank
        @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
        String title,

        @NotNull
        @PositiveOrZero
        BigDecimal price,

        String description,

        List<MultipartFile> media,

        Long categoryId) {
}

