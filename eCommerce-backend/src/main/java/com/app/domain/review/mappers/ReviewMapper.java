package com.app.domain.review.mappers;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.entities.base.Review;

import static com.app.domain.member.mappers.MemberMapper.toMemberSummaryDTO;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewDTO toReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                toMemberSummaryDTO(review.getAuthor()),
                review.getRating(),
                CommentMapper.toCommentDTO(review.getComment()),
                review.getCreatedDate(),
                review.getLastModifiedDate()
        );
    }
}
