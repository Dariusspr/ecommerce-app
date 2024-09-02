package com.app.domain.review.dtos.requests;

import jakarta.validation.constraints.*;

import static com.app.global.constants.UserInputConstants.*;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;

public record MemberReviewRequest(
        @NotNull
        @PositiveOrZero
        Long memberId,

        @NotNull
        @Min(RATING_MIN)
        @Max(RATING_MAX)
        int rating,

        @NotBlank
        @Size(min = COMMENT_CONTENT_LENGTH_MIN,
                max = COMMENT_CONTENT_LENGTH_MAX)
        String content) {
}

