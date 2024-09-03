package com.app.domain.review.controllers.publ;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.services.ItemReviewService;
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

import java.util.UUID;

@RestController("publItemReviewController")
@RequestMapping(ItemReviewController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class ItemReviewController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/item-reviews";

    private final ItemReviewService reviewService;

    public ItemReviewController(ItemReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping("/item/{itemId}")
    public ResponseEntity<Page<ReviewDTO>> getAllByItemId(@PathVariable("itemId") @NotNull UUID itemId,
                                                          Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllByItemId(itemId, pageable));
    }

    @RequestMapping("/author/{authorId}")
    public ResponseEntity<Page<ReviewDTO>> getAllByAuthorId(@PathVariable("authorId") @NotNull Long authorId,
                                                            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getAllByAuthorId(authorId, pageable));
    }

    @RequestMapping("/item/count/{itemId}")
    public ResponseEntity<Long> getCountByItemId(@PathVariable("itemId") @NotNull UUID itemId) {
        return ResponseEntity.ok(reviewService.getReviewCountByItemId(itemId));
    }

    @RequestMapping("/author/count/{authorId}")
    public ResponseEntity<Long> getCountByAuthorId(@PathVariable("authorId")
                                                   @NotNull
                                                   @PositiveOrZero
                                                   Long authorId) {
        return ResponseEntity.ok(reviewService.getReviewCountByAuthorId(authorId));
    }

    @RequestMapping("/item/rating/{itemId}")
    public ResponseEntity<Float> getRatingByItemId(@PathVariable("itemId") @NotNull UUID itemId) {
        return ResponseEntity.ok(reviewService.getAverageReviewRatingByItemId(itemId));
    }
}