package com.app.domain.review.controllers.members;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.MemberReviewRequest;
import com.app.domain.review.dtos.requests.ModifyReviewRequest;
import com.app.domain.review.services.MemberReviewService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController("membersMemberReviewController")
@RequestMapping(MemberReviewController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class MemberReviewController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/member-reviews";

    private final MemberReviewService reviewService;

    public MemberReviewController(MemberReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> create(
            @Validated
            @RequestBody
            MemberReviewRequest request) {
        return ResponseEntity.ok(reviewService.create(request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteById(
            @PathVariable("reviewId")
            @NotNull
            @PositiveOrZero
            Long reviewId) {
        reviewService.deleteById(reviewId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> modify(
            @PathVariable("reviewId")
            @NotNull
            @PositiveOrZero
            Long reviewId,
            @RequestBody
            @Validated
            ModifyReviewRequest request) {
        return ResponseEntity.ok(reviewService.modify(reviewId, request));
    }
}
