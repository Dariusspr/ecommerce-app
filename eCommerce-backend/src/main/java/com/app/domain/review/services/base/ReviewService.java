package com.app.domain.review.services.base;

import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.ModifyReviewRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.base.Review;
import com.app.domain.review.exceptions.DuplicateReviewException;
import com.app.domain.review.exceptions.ReviewNotFoundException;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;


public abstract class ReviewService<R extends JpaRepository<T, Long>, T extends Review> {

    protected final R reviewRepository;

    protected ReviewService(R reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public T getById(Long id) {
        return reviewRepository.findById(id).orElseThrow(ReviewNotFoundException::new);
    }

    @Transactional
    public T save(T entity) {
        try {
            return reviewRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateReviewException();
        }
    }

    @Transactional
    public ReviewDTO modify(Long reviewId, ModifyReviewRequest request) {
        T review = getById(reviewId);
        if (AuthUtils.isNotAllowedModifier(review.getAuthor())) {
            throw new ForbiddenException();
        }
        updateReview(review, request);
        return ReviewMapper.toReviewDTO(save(review));
    }

    @Transactional
    public void deleteById(Long id) {
        T review = getById(id);
        if (AuthUtils.isNotAllowedModifier(review.getAuthor())) {
            throw new ForbiddenException();
        }
        reviewRepository.delete(review);
    }

    private void updateReview(Review review, ModifyReviewRequest request) {
        if (request.rating() != null) {
            review.setRating(request.rating());
        }
        if (request.content() != null) {
            Comment comment = review.getComment();
            comment.setContent(request.content());
        }
    }
}
