package com.app.domain.item.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public record NewCategoryRequest(
        @PositiveOrZero
        Long parentId,

        @NotBlank
        @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
        String title) {
}
