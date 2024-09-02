package com.app.domain.review.services;


import com.app.domain.item.entities.Item;
import com.app.domain.item.services.ItemService;
import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.ItemReviewRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.ItemReview;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.repositories.ItemReviewRepository;
import com.app.domain.review.services.base.ReviewService;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ItemReviewService extends ReviewService<ItemReviewRepository, ItemReview> {

    private final ItemService itemService;
    private final MemberService memberService;

    public ItemReviewService(ItemReviewRepository reviewRepository, ItemService itemService, MemberService memberService) {
        super(reviewRepository);
        this.itemService = itemService;
        this.memberService = memberService;
    }
    @Transactional
    public ReviewDTO create(ItemReviewRequest request) {
        Item item = itemService.findById(request.itemId());
        Member author = AuthUtils.getAuthenticated();
        Comment comment = new Comment(author, request.content());
        ItemReview review = new ItemReview(author, request.rating(), comment, item);
        return ReviewMapper.toReviewDTO(save(review));
    }

    @Transactional
    public void deleteAllByItemId(UUID itemId) {
        Item item = itemService.findById(itemId);
        if (AuthUtils.isNotAllowedModifier(item.getSeller())) {
            throw new ForbiddenException();
        }
        reviewRepository.deleteAllByItem(item);
    }

    @Transactional
    public void deleteAllByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);
        if (AuthUtils.isNotAllowedModifier(author)) {
            throw new ForbiddenException();
        }
        reviewRepository.deleteAllByAuthor(author);
    }

    public Page<ReviewDTO> getAllByItemId(UUID itemId, Pageable pageable) {
        Item item = itemService.findById(itemId);
        Page<ItemReview> itemReviewPage = reviewRepository.findAllByItem(item, pageable);
        return itemReviewPage.map(ReviewMapper::toReviewDTO);
    }

    public Page<ReviewDTO> getAllByAuthorId(Long authorId, Pageable pageable) {
        Member author = memberService.findById(authorId);
        Page<ItemReview> itemReviewPage = reviewRepository.findAllByAuthor(author, pageable);
        return itemReviewPage.map(ReviewMapper::toReviewDTO);
    }

    public long getReviewCountByItemId(UUID itemId) {
        Item item = itemService.findById(itemId);
        return reviewRepository.findReviewCountByItem(item);
    }

    public long getReviewCountByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);

        return reviewRepository.findReviewCountByAuthor(author);
    }

    public float getAverageReviewRatingByItemId(UUID itemId) {
        Item item = itemService.findById(itemId);
        return reviewRepository.findAverageReviewRatingByItem(item);
    }
}
