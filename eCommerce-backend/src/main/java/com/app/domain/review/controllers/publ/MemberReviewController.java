package com.app.domain.review.controllers.publ;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.services.MemberReviewService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("publMemberReviewController")
@RequestMapping(MemberReviewController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class MemberReviewController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/member-reviews";

    private final MemberReviewService reviewService;

    public MemberReviewController(MemberReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping("/member/{memberId}")
    public ResponseEntity<Page<ReviewDTO>> getAllByMemberId(@PathVariable
                                                            @NotNull
                                                            @PositiveOrZero
                                                            Long memberId,
                                                            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllByMemberId(memberId, pageable));
    }

    @RequestMapping("/author/{authorId}")
    public ResponseEntity<Page<ReviewDTO>> getAllByAuthorId(@PathVariable
                                                            @NotNull
                                                            @PositiveOrZero
                                                            Long authorId,
                                                            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllByAuthorId(authorId, pageable));
    }

    @RequestMapping("/member/count/{memberId}")
    public ResponseEntity<Long> getCountByMemberId(@PathVariable
                                                   @NotNull
                                                   @PositiveOrZero
                                                   Long memberId) {
        return ResponseEntity.ok(reviewService.getReviewCountByMemberId(memberId));
    }

    @RequestMapping("/author/count/{authorId}")
    public ResponseEntity<Long> getCountByAuthorId(@PathVariable
                                                   @NotNull
                                                   @PositiveOrZero
                                                   Long authorId) {
        return ResponseEntity.ok(reviewService.getReviewCountByAuthorId(authorId));
    }

    @RequestMapping("/member/rating/{memberId}")
    public ResponseEntity<Float> getRatingByItemId(@PathVariable
                                                   @NotNull
                                                   @PositiveOrZero
                                                   Long memberId) {
        return ResponseEntity.ok(reviewService.getAverageReviewRatingByMemberId(memberId));
    }
}