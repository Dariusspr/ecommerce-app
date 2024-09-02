package com.app.domain.review.dtos;

import com.app.domain.review.enums.ReactionType;

public record CommentReactionsInfoDTO(ReactionType reactionType, long count) {
}
