package com.app.domain.review.dtos.requests;

import jakarta.validation.constraints.*;

import java.util.UUID;

import static com.app.global.constants.UserInputConstants.*;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;

public record ItemReviewRequest(
        @NotNull
        @PositiveOrZero
        UUID itemId,

        @NotNull
        @Min(RATING_MIN)
        @Max(RATING_MAX)
        int rating,

        @Size(min = COMMENT_CONTENT_LENGTH_MIN,
                max = COMMENT_CONTENT_LENGTH_MAX)
        String content) {
}
