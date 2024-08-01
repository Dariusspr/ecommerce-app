package com.app.domain.review.mappers;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.entities.Review;

import static com.app.domain.member.mappers.MemberMapper.toMemberSummaryDTO;
import static com.app.domain.review.mappers.CommentMapper.toTopLevelCommentDTO;

public class ReviewMapper {

    private ReviewMapper() {
    }

    public static ReviewDTO toReviewDTO(Review review) {
        return new ReviewDTO(
                review.getId(),
                toMemberSummaryDTO(review.getAuthor()),
                review.getRating(),
                toTopLevelCommentDTO(review.getComment()),
                review.getCreatedDate(),
                review.getLastModifiedDate()
        );
    }
}
