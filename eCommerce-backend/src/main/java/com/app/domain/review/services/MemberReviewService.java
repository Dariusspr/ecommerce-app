package com.app.domain.review.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.MemberReviewRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.MemberReview;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.repositories.MemberReviewRepository;
import com.app.domain.review.services.base.ReviewService;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberReviewService extends ReviewService<MemberReviewRepository, MemberReview> {

    private final MemberService memberService;

    protected MemberReviewService(MemberReviewRepository reviewRepository, MemberService memberService) {
        super(reviewRepository);
        this.memberService = memberService;
    }

    @Transactional
    public ReviewDTO create(MemberReviewRequest request) {
        Member member = memberService.findById(request.memberId());
        Member author = AuthUtils.getAuthenticated();
        Comment comment = new Comment(author, request.content());
        MemberReview review = new MemberReview(author, request.rating(), comment, member);
        return ReviewMapper.toReviewDTO(save(review));
    }

    @Transactional
    public void deleteAllByMemberId(Long memberId) {
        Member member = memberService.findById(memberId);
        if (AuthUtils.isNotAllowedModifier(member)) {
            throw new ForbiddenException();
        }
        reviewRepository.deleteAllByMember(member);
    }

    @Transactional
    public void deleteAllByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);
        if (AuthUtils.isNotAllowedModifier(author)) {
            throw new ForbiddenException();
        }
        reviewRepository.deleteAllByAuthor(author);
    }

    public Page<ReviewDTO> getAllByMemberId(Long memberId, Pageable pageable) {
        Member member = memberService.findById(memberId);
        Page<MemberReview> memberReviewPage = reviewRepository.findAllByMember(member, pageable);
        return memberReviewPage.map(ReviewMapper::toReviewDTO);
    }

    public Page<ReviewDTO> getAllByAuthorId(Long authorId, Pageable pageable) {
        Member author = memberService.findById(authorId);
        Page<MemberReview> memberReviewPage = reviewRepository.findAllByAuthor(author, pageable);
        return memberReviewPage.map(ReviewMapper::toReviewDTO);
    }

    public long getReviewCountByMemberId(Long memberId) {
        Member member = memberService.findById(memberId);
        return reviewRepository.findReviewCountByMember(member);
    }

    public long getReviewCountByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);
        return reviewRepository.findReviewCountByAuthor(author);
    }

    public float getAverageReviewRatingByMemberId(Long memberId) {
        Member member = memberService.findById(memberId);
        return reviewRepository.findAverageReviewRatingByMember(member);
    }
}
