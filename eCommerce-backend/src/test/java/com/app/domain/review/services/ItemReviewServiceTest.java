package com.app.domain.review.services;


import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.repositories.ItemRepository;
import com.app.domain.item.services.ItemService;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.ItemReviewRequest;
import com.app.domain.review.dtos.requests.ModifyReviewRequest;
import com.app.domain.review.entities.ItemReview;
import com.app.domain.review.exceptions.DuplicateReviewException;
import com.app.domain.review.exceptions.ReviewNotFoundException;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.repositories.CommentRepository;
import com.app.domain.review.repositories.ItemReviewRepository;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.item.RandomItemBuilder;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ItemReviewServiceTest {

    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemReviewService itemReviewService;
    @Autowired
    private ItemReviewRepository itemReviewRepository;
    @Autowired
    private CommentRepository commentRepository;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member author;
    private Item item;

    @BeforeEach
    void setupAuthorAndItem() {
        author = new RandomMemberBuilder().create();
        memberService.save(author);

        Member seller = new RandomMemberBuilder().create();
        memberService.save(seller);
        item = new RandomItemBuilder(seller).create();
        itemService.save(item);

    }

    @AfterEach
    void clear() {
        itemReviewRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        memberRepository.deleteAll();
    }


    @Test
    void save_ok() {
        ItemReview review = getItemReview();

        ItemReview returnedReview = itemReviewService.save(review);

        assertNotNull(returnedReview.getId());
    }

    @Test
    void save_throwDuplicateReview() {
        ItemReview review = getItemReview();
        ItemReview anotherReview = getItemReview();
        itemReviewService.save(review);

        assertThrows(DuplicateReviewException.class,
                () -> itemReviewService.save(anotherReview));
    }

    @Test
    void getById_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        ItemReview returnedReview = itemReviewService.getById(review.getId());

        assertEquals(review.getId(), returnedReview.getId());
    }

    @Test
    void getById_throwReviewNotFound() {
        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.getById(NumberUtils.getId()));
    }

    @Test
    void modify_ok() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        itemReviewService.save(review);
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        ReviewDTO returnedReviewDTO = itemReviewService.modify(review.getId(), request);

        assertEquals(reviewDTO.id(), returnedReviewDTO.id());
        assertEquals(reviewDTO.author(), returnedReviewDTO.author());
        assertEquals(request.rating(), returnedReviewDTO.rating());
        assertEquals(request.content(), returnedReviewDTO.comment().content());
    }

    @Test
    void modify_throwReviewNotFound() {
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.modify(NumberUtils.getId(), request));
    }

    @Test
    void modify_throwForbidden() {
        mockAuthenticationForbidden();
        ItemReview review = getItemReview();
        itemReviewService.save(review);
        ModifyReviewRequest request = new ModifyReviewRequest(RandomReviewBuilder.getRating(), "new content");

        assertThrows(ForbiddenException.class,
                () -> itemReviewService.modify(review.getId(), request));
    }

    @Test
    void deleteById_ok() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertDoesNotThrow(() -> itemReviewService.deleteById(review.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.getById(review.getId()));
    }

    @Test
    void deleteById_throwReviewNotFound() {
        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.deleteById(NumberUtils.getId()));
    }

    @Test
    void deleteById_throwForbidden() {
        mockAuthenticationForbidden();
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> itemReviewService.deleteById(review.getId()));

    }

    @Test
    void deleteAllByItemId_ok() {
        Member seller = item.getSeller();
        mockAuthentication(seller);
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertDoesNotThrow(() -> itemReviewService.deleteAllByItemId(item.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.getById(review.getId()));
    }

    @Test
    void deleteAllByItemId_throwItemNotFound() {
        assertThrows(ItemNotFoundException.class,
                () -> itemReviewService.deleteAllByItemId(UUID.randomUUID()));
    }

    @Test
    void deleteAllByItemId_throwForbidden() {
        mockAuthenticationForbidden();
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> itemReviewService.deleteAllByItemId(item.getId()));
    }

    @Test
    void deleteAllByAuthorId_ok() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertDoesNotThrow(() -> itemReviewService.deleteAllByAuthorId(author.getId()));
        assertThrows(ReviewNotFoundException.class,
                () -> itemReviewService.getById(review.getId()));
    }

    @Test
    void deleteAllByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> itemReviewService.deleteAllByAuthorId(NumberUtils.getId()));
    }

    @Test
    void deleteAllByAuthorId_throwForbidden() {
        mockAuthenticationForbidden();
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        assertThrows(ForbiddenException.class,
                () -> itemReviewService.deleteAllByAuthorId(author.getId()));
    }

    @Test
    void create_ok() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);
        ItemReviewRequest request = new ItemReviewRequest(item.getId(), review.getRating(),
                review.getComment().getContent());

        ReviewDTO returnedReviewDTO = itemReviewService.create(request);

        assertNotNull(returnedReviewDTO.id());
        assertEquals(reviewDTO.author(), returnedReviewDTO.author());
        assertEquals(reviewDTO.rating(), returnedReviewDTO.rating());
        assertEquals(reviewDTO.comment().author(), returnedReviewDTO.comment().author());
    }

    @Test
    void create_throwDuplicateReview() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        ItemReviewRequest request = new ItemReviewRequest(item.getId(), review.getRating(),
                review.getComment().getContent());
        itemReviewService.save(review);

        assertThrows(DuplicateReviewException.class,
                () -> itemReviewService.create(request));
    }

    @Test
    void create_throwItemNotFound() {
        mockAuthentication(author);
        ItemReview review = getItemReview();
        ItemReviewRequest request = new ItemReviewRequest(UUID.randomUUID(), review.getRating(),
                review.getComment().getContent());

        assertThrows(ItemNotFoundException.class,
                () -> itemReviewService.create(request));
    }

    @Test
    void getAllByItemId_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);
        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);

        Page<ReviewDTO> page = itemReviewService.getAllByItemId(item.getId(), Pageable.ofSize(1));

        assertEquals(1, page.getTotalElements());
        assertEquals(reviewDTO, page.getContent().getFirst());
    }

    @Test
    void getAllByItemId_throwItemNotFound() {
        assertThrows(ItemNotFoundException.class,
                () -> itemReviewService.getAllByItemId(UUID.randomUUID(), Pageable.ofSize(1)));
    }

    @Test
    void getAllByAuthorId_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        ReviewDTO reviewDTO = ReviewMapper.toReviewDTO(review);

        Page<ReviewDTO> page = itemReviewService.getAllByAuthorId(author.getId(), Pageable.ofSize(1));

        assertEquals(1, page.getTotalElements());
        assertEquals(reviewDTO, page.getContent().getFirst());
    }

    @Test
    void getAllByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> itemReviewService.getAllByAuthorId(NumberUtils.getId(), Pageable.ofSize(1)));
    }

    @Test
    void getReviewCountByItemId_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        long count = itemReviewService.getReviewCountByItemId(item.getId());

        assertEquals(1, count);
    }

    @Test
    void getReviewCountByItemId_throwItemNotFound() {
        assertThrows(ItemNotFoundException.class,
                () -> itemReviewService.getReviewCountByItemId(UUID.randomUUID()));
    }

    @Test
    void getReviewCountByAuthorId_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        long count = itemReviewService.getReviewCountByAuthorId(author.getId());

        assertEquals(1, count);
    }

    @Test
    void getReviewCountByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> itemReviewService.getReviewCountByAuthorId(NumberUtils.getId()));
    }

    @Test
    void getAverageRatingByItemId_single_ok() {
        ItemReview review = getItemReview();
        itemReviewService.save(review);

        float avgRating = itemReviewService.getAverageReviewRatingByItemId(item.getId());

        assertEquals(review.getRating(), avgRating);
    }

    @Test
    void getAverageRatingByItemId_two_ok() {
        ItemReview review = getItemReview();
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        ItemReview review2 = getItemReview();
        review2.setAuthor(member);
        itemReviewService.save(review);
        itemReviewService.save(review2);
        float actualAvg = (float) (review.getRating() + review2.getRating()) / 2;

        float returnedAvg = itemReviewService.getAverageReviewRatingByItemId(item.getId());

        assertEquals(actualAvg, returnedAvg);
    }

    @Test
    void getAverageRatingByItemId_throwItemNotFound() {
        assertThrows(ItemNotFoundException.class,
                () -> itemReviewService.getAverageReviewRatingByItemId(UUID.randomUUID()));
    }


    private void mockAuthenticationForbidden() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        mockAuthentication(member);
    }

    private ItemReview getItemReview() {
        return (ItemReview) new RandomReviewBuilder().withCustomAuthor(author).create(item);
    }

    private void mockAuthentication(Member authenticatedMember) {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(authenticatedMember);
    }
}
