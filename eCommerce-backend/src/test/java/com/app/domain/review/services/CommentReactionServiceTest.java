package com.app.domain.review.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.CommentReactionsInfoDTO;
import com.app.domain.review.dtos.requests.CommentReactionRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.CommentReaction;
import com.app.domain.review.enums.ReactionType;
import com.app.domain.review.exceptions.CommentNotFoundException;
import com.app.domain.review.exceptions.CommentReactionNotFoundException;
import com.app.domain.review.exceptions.DuplicateCommentReactionException;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.domain.review.RandomCommentBuilder;
import com.app.utils.domain.review.RandomCommentReactionBuilder;
import com.app.utils.global.NumberUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CommentReactionServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberService memberService;
    @Autowired
    CommentReactionService commentReactionService;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member author;
    private Comment comment;
    private CommentReaction reaction;

    @BeforeEach
    void setup() {
        author = new RandomMemberBuilder().create();
        memberService.save(author);

        comment = new RandomCommentBuilder(author).create();
        commentService.save(comment);

        reaction = new RandomCommentReactionBuilder()
                .withCustomAuthor(author)
                .withCustomComment(comment).create();
    }

    @Test
    void create_ok() {
        mockAuthentication(author);
        CommentReactionRequest request = new CommentReactionRequest(comment.getId(), reaction.getReactionType());

        List<CommentReactionsInfoDTO> currentReactions = commentReactionService.create(request);

        assertEquals(1, currentReactions.size());
        assertEquals(1, currentReactions.getFirst().count());
        assertEquals(reaction.getReactionType(), currentReactions.getFirst().reactionType());
    }

    @Test
    void create_throwCommentNotFound() {
        mockAuthentication(author);
        CommentReactionRequest request = new CommentReactionRequest(NumberUtils.getId(), reaction.getReactionType());

        assertThrows(CommentNotFoundException.class,
                () -> commentReactionService.create(request));
    }

    @Test
    void create_throwDuplicateReaction() {
        mockAuthentication(author);
        commentReactionService.save(reaction);
        CommentReactionRequest request = new CommentReactionRequest(comment.getId(), reaction.getReactionType());

        assertThrows(DuplicateCommentReactionException.class,
                () -> commentReactionService.create(request));
    }

    @Test
    void modify_whenSingle_ok() {
        mockAuthentication(author);
        commentReactionService.save(reaction);
        ReactionType newReactionType = reaction.getReactionType() == ReactionType.LIKE
                ? ReactionType.DISLIKE : ReactionType.LIKE;

        List<CommentReactionsInfoDTO> currentReactions = commentReactionService.modify(reaction.getId(), newReactionType);

        assertEquals(1, currentReactions.size());
        assertEquals(newReactionType, currentReactions.getFirst().reactionType());
        assertEquals(1, currentReactions.getFirst().count());
    }

    @Test
    void modify_throwCommentReactionNotFound() {
        mockAuthentication(author);

        assertThrows(CommentReactionNotFoundException.class,
                () -> commentReactionService.modify(NumberUtils.getId(), ReactionType.DISLIKE));
    }

    @Test
    void modify_throwForbidden() {
        mockAuthenticationForbidden();
        commentReactionService.save(reaction);

        assertThrows(ForbiddenException.class,
                () -> commentReactionService.modify(reaction.getId(), ReactionType.DISLIKE));
    }

    @Test
    void deleteById_ok() {
        mockAuthentication(author);
        commentReactionService.save(reaction);

        assertDoesNotThrow(() -> commentReactionService.deleteById(reaction.getId()));
        assertThrows(CommentReactionNotFoundException.class,
                () -> commentReactionService.getById(reaction.getId()));

    }

    @Test
    void deleteById_throwForbidden() {
        mockAuthenticationForbidden();
        commentReactionService.save(reaction);

        assertThrows(ForbiddenException.class,
                () -> commentReactionService.deleteById(reaction.getId()));
    }


    @Test
    void deleteById_throwCommentReactionNotFound() {
        assertThrows(CommentReactionNotFoundException.class,
                () -> commentReactionService.deleteById(NumberUtils.getId()));
    }

    @Test
    void deleteAllByAuthorId_ok() {
        mockAuthentication(author);
        commentReactionService.save(reaction);

        assertDoesNotThrow(() -> commentReactionService.deleteAllByAuthorId(author.getId()));
        assertThrows(CommentReactionNotFoundException.class,
                () -> commentReactionService.getById(reaction.getId()));

    }

    @Test
    void deleteAllByAuthorId_throwForbidden() {
        mockAuthenticationForbidden();
        commentReactionService.save(reaction);

        assertThrows(ForbiddenException.class,
                () -> commentReactionService.deleteAllByAuthorId(author.getId()));
    }

    @Test
    void deleteAllByAuthorId_throwCommentReactionNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> commentReactionService.deleteAllByAuthorId(NumberUtils.getId()));
    }

    @Test
    void deleteComment_cascadeDeleteReactions() {
        mockAuthentication(author);
        commentReactionService.save(reaction);

        commentService.deleteById(comment.getId());
        assertThrows(CommentReactionNotFoundException.class, () -> commentReactionService.getById(reaction.getId()));
    }

    @Test
    void save_ok() {
        CommentReaction returnedReaction = commentReactionService.save(reaction);

        assertNotNull(returnedReaction.getId());
    }

    @Test
    void save_throwDuplicateReaction() {
        commentReactionService.save(reaction);
        CommentReaction anotherReaction = new RandomCommentReactionBuilder()
                .withCustomAuthor(author)
                .withCustomComment(comment)
                .create();
        assertDoesNotThrow(() -> commentReactionService.save(reaction));
        assertThrows(DuplicateCommentReactionException.class,
                () -> commentReactionService.save(anotherReaction));
    }

    @Test
    void getAllByCommentId_single_ok() {
        commentReactionService.save(reaction);

        List<CommentReactionsInfoDTO> reactions = commentReactionService.getAllByCommentId(comment.getId());

        assertEquals(1, reactions.size());
        assertEquals(1, reactions.getFirst().count());
        assertEquals(reaction.getReactionType(), reactions.getFirst().reactionType());
    }

    @Test
    void getAllByCommentId_two_ok() {
        commentReactionService.save(reaction);
        ReactionType anotherReactionType = reaction.getReactionType() == ReactionType.LIKE
                ? ReactionType.DISLIKE : ReactionType.LIKE;
        CommentReaction anotherReaction = new RandomCommentReactionBuilder()
                .withCustomComment(comment)
                .withCustomReactionType(anotherReactionType)
                .create();
        memberService.save(anotherReaction.getAuthor());
        commentReactionService.save(anotherReaction);

        List<CommentReactionsInfoDTO> reactions = commentReactionService.getAllByCommentId(comment.getId());

        assertEquals(2, reactions.size());
        assertEquals(1, reactions.getFirst().count());
        assertEquals(1, reactions.getLast().count());
        assertNotSame(reactions.getFirst().reactionType(),
                reactions.getLast().reactionType());
    }

    @Test
    void getAllByCommentId_throwCommentNotFound() {
        assertThrows(CommentNotFoundException.class,
                () -> commentReactionService.getAllByCommentId(NumberUtils.getId()));
    }

    @Test
    void getCountByCommentId_single_ok() {
        commentReactionService.save(reaction);

        long count = commentReactionService.getCountByCommentId(comment.getId());

        assertEquals(1, count);
    }

    @Test
    void getCountByCommentId_two_ok() {
        final int reactionCount = 2;
        commentReactionService.save(reaction);
        CommentReaction anotherReaction = new RandomCommentReactionBuilder()
                .withCustomComment(comment)
                .create();
        memberService.save(anotherReaction.getAuthor());
        commentReactionService.save(anotherReaction);

        long count = commentReactionService.getCountByCommentId(comment.getId());

        assertEquals(reactionCount, count);
    }

    @Test
    void getCountByCommentId_throwCommentNotFound() {
        assertThrows(CommentNotFoundException.class,
                () -> commentReactionService.getCountByCommentId(NumberUtils.getId()));
    }

    @Test
    void getById_ok() {
        commentReactionService.save(reaction);

        CommentReaction returned = commentReactionService.getById(reaction.getId());

        assertEquals(reaction.getId(), returned.getId());
    }

    @Test
    void getById_throwCommentReactionNotFound() {
        assertThrows(CommentReactionNotFoundException.class,
                () -> commentReactionService.getById(NumberUtils.getId()));
    }

    private void mockAuthenticationForbidden() {
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        mockAuthentication(member);
    }

    private void mockAuthentication(Member authenticatedMember) {
        SecurityContextHolder.setContext(securityContext);
        given(securityContext.getAuthentication()).willReturn(authentication);
        given(authentication.getPrincipal()).willReturn(authenticatedMember);
    }
}

