package com.app.domain.review.services;


import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.MemberReviewRequest;
import com.app.domain.review.dtos.requests.ModifyReviewRequest;
import com.app.domain.review.entities.MemberReview;
import com.app.domain.review.exceptions.DuplicateReviewException;
import com.app.domain.review.exceptions.ReviewNotFoundException;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.repositories.CommentRepository;
import com.app.domain.review.repositories.MemberReviewRepository;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.domain.review.RandomReviewBuilder;
import com.app.utils.global.NumberUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class MemberReviewServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberReviewService memberReviewService;
    @Autowired
    private MemberReviewRepository memberReviewRepository;
    @Autowired
    private CommentRepository commentRepository;


    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member author;
    private Member member;

    @BeforeEach
    void setupAuthorAndItem() {
        author = new RandomMemberBuilder().create();
        memberService.save(author);

        member = new RandomMemberBuilder().create();
        memberService.save(member);
    }

    @AfterEach
    void clear() {
        memberReviewRepository.deleteAll();
        commentRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void save_ok() {
        MemberReview review = getMemberReview();

        MemberReview returnedReview = memberReviewService.save(review);

        assertNotNull(returnedReview.getId());
    }

    @Test
    void save_throwDuplicateReview() {
        MemberReview review = getMemberReview();
        MemberReview anotherReview = getMemberReview();
        memberReviewService.save(review);

        assertThrows(DuplicateReviewException.class,
                () -> memberReviewService.save(anotherReview));
    }

    @Test
    void getById_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        MemberReview returnedReview = memberReviewService.getById(review.getId());

        assertEquals(review.getId(), returnedReview.getId());
    }

    @Test
    void getById_throwReviewNotFound() {
        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.getById(NumberUtils.getId()));
    }

    @Test
    void modify_ok() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        memberReviewService.save(review);
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        ReviewDTO returnedReviewDTO = memberReviewService.modify(review.getId(), request);

        assertEquals(reviewDTO.id(), returnedReviewDTO.id());
        assertEquals(reviewDTO.author(), returnedReviewDTO.author());
        assertEquals(request.rating(), returnedReviewDTO.rating());
        assertEquals(request.content(), returnedReviewDTO.comment().content());
    }

    @Test
    void modify_throwReviewNotFound() {
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.modify(NumberUtils.getId(), request));
    }

    @Test
    void modify_throwForbidden() {
        mockAuthenticationForbidden();
        MemberReview review = getMemberReview();
        memberReviewService.save(review);
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        assertThrows(ForbiddenException.class,
                () -> memberReviewService.modify(review.getId(), request));
    }

    @Test
    void deleteById_ok() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertDoesNotThrow(() -> memberReviewService.deleteById(review.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.getById(review.getId()));
    }

    @Test
    void deleteById_throwReviewNotFound() {
        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.deleteById(NumberUtils.getId()));
    }

    @Test
    void deleteById_throwForbidden() {
        mockAuthenticationForbidden();
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> memberReviewService.deleteById(review.getId()));

    }

    @Test
    void deleteAllByMemberId_ok() {
        mockAuthentication(member);
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertDoesNotThrow(() -> memberReviewService.deleteAllByMemberId(member.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.getById(review.getId()));
    }

    @Test
    void deleteAllByMemberId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.deleteAllByMemberId(NumberUtils.getId()));
    }

    @Test
    void deleteAllByItemId_throwForbidden() {
        mockAuthenticationForbidden();
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> memberReviewService.deleteAllByMemberId(member.getId()));
    }

    @Test
    void deleteAllByAuthorId_ok() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertDoesNotThrow(() -> memberReviewService.deleteAllByAuthorId(author.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> memberReviewService.getById(review.getId()));
    }

    @Test
    void deleteAllByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.deleteAllByAuthorId(NumberUtils.getId()));
    }

    @Test
    void deleteAllByAuthorId_throwForbidden() {
        mockAuthenticationForbidden();
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> memberReviewService.deleteAllByAuthorId(author.getId()));
    }

    @Test
    void create_ok() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);
        MemberReviewRequest request = new MemberReviewRequest(member.getId(), review.getRating(),
                review.getComment().getContent());

        ReviewDTO returnedReviewDTO = memberReviewService.create(request);

        assertNotNull(returnedReviewDTO.id());
        assertEquals(reviewDTO.author(), returnedReviewDTO.author());
        assertEquals(reviewDTO.rating(), returnedReviewDTO.rating());
        assertEquals(reviewDTO.comment().author(), returnedReviewDTO.comment().author());
    }

    @Test
    void create_throwDuplicateReview() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        MemberReviewRequest request = new MemberReviewRequest(member.getId(), review.getRating(),
                review.getComment().getContent());
        memberReviewService.save(review);

        assertThrows(DuplicateReviewException.class,
                () -> memberReviewService.create(request));
    }

    @Test
    void create_throwMemberNotFound() {
        mockAuthentication(author);
        MemberReview review = getMemberReview();
        MemberReviewRequest request = new MemberReviewRequest(NumberUtils.getId(), review.getRating(),
                review.getComment().getContent());

        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.create(request));
    }

    @Test
    void getAllByMemberId_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);

        Page<ReviewDTO> page = memberReviewService.getAllByMemberId(member.getId(), Pageable.ofSize(1));

        assertEquals(1, page.getTotalElements());
        assertEquals(reviewDTO, page.getContent().getFirst());
    }

    @Test
    void getAllByMemberId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.getAllByMemberId(NumberUtils.getId(), Pageable.ofSize(1)));
    }

    @Test
    void getAllByAuthorId_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);

        Page<ReviewDTO> page = memberReviewService.getAllByAuthorId(author.getId(), Pageable.ofSize(1));

        assertEquals(1, page.getTotalElements());
        assertEquals(reviewDTO, page.getContent().getFirst());
    }

    @Test
    void getAllByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.getAllByAuthorId(NumberUtils.getId(), Pageable.ofSize(1)));
    }

    @Test
    void getReviewCountByMemberId_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        long count = memberReviewService.getReviewCountByMemberId(member.getId());

        assertEquals(1, count);
    }

    @Test
    void getReviewCountByMemberId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.getReviewCountByMemberId(NumberUtils.getId()));
    }

    @Test
    void getReviewCountByAuthorId_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        long count = memberReviewService.getReviewCountByAuthorId(author.getId());

        assertEquals(1, count);
    }

    @Test
    void getReviewCountByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.getReviewCountByAuthorId(NumberUtils.getId()));
    }

    @Test
    void getAverageRatingByMemberId_single_ok() {
        MemberReview review = getMemberReview();
        memberReviewService.save(review);

        float avgRating = memberReviewService.getAverageReviewRatingByMemberId(member.getId());

        assertEquals(review.getRating(), avgRating);
    }

    @Test
    void getAverageRatingByMemberId_two_ok() {
        MemberReview review = getMemberReview();
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        MemberReview review2 = getMemberReview();
        review2.setAuthor(member);
        memberReviewService.save(review);
        memberReviewService.save(review2);
        float actualAvg = (float) (review.getRating() + review2.getRating()) / 2;

        float returnedAvg = memberReviewService.getAverageReviewRatingByMemberId(review2.getMember().getId());

        assertEquals(actualAvg, returnedAvg);
    }

    @Test
    void getAverageRatingByMemberId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> memberReviewService.getAverageReviewRatingByMemberId(NumberUtils.getId()));
    }


    private void mockAuthenticationForbidden() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        mockAuthentication(member);
    }

    private MemberReview getMemberReview() {
        return (MemberReview) new RandomReviewBuilder().withCustomAuthor(author).create(member);
    }

    private void mockAuthentication(Member authenticatedMember) {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(authenticatedMember);
    }
}