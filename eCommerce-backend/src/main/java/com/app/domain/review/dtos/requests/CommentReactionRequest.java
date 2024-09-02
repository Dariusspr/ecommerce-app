package com.app.domain.review.dtos.requests;

import com.app.domain.review.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CommentReactionRequest (
        @NotNull
        @PositiveOrZero
        Long commentId,

        @NotNull
        ReactionType reactionType) {
}
