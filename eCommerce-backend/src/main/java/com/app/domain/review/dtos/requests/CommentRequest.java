package com.app.domain.review.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MIN;

public record CommentRequest(
        @NotNull
        @PositiveOrZero
        Long parentId,

        @NotBlank
        @Size(min = COMMENT_CONTENT_LENGTH_MIN, max = COMMENT_CONTENT_LENGTH_MAX)
        String content) {
}
