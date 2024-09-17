package com.app.domain.review.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.repositories.MemberRepository;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.dtos.requests.CommentRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.exceptions.CommentNotFoundException;
import com.app.domain.review.exceptions.ParentCommentNotFoundException;
import com.app.domain.review.mappers.CommentMapper;
import com.app.domain.review.repositories.CommentRepository;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.domain.review.RandomCommentBuilder;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.app.utils.domain.review.RandomCommentBuilder.CHILDREN_COUNT_MAX;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class CommentServiceTest {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private Authentication authentication;
    @MockBean
    private SecurityContext securityContext;

    private Member author;

    @BeforeEach
    void setupAuthor() {
        author = new RandomMemberBuilder().create();
        memberService.save(author);
    }

    @AfterEach
    void clear() {
        commentRepository.deleteAll();
        memberRepository.deleteAll();
    }

    @Test
    void create_noParent_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).create();
        CommentRequest request = new CommentRequest(null, comment.getContent());

        CommentDTO returnedCommentDto = commentService.create(request);

        assertNotNull(returnedCommentDto.id());
        assertNull(returnedCommentDto.parentId());
        assertEquals(comment.getContent(), returnedCommentDto.content());
        assertEquals(author.getId(), returnedCommentDto.author().id());
    }

    @Test
    void create_withParent_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).withParent().create();
        Comment parent = commentService.save(comment.getParent());
        CommentRequest request = new CommentRequest(parent.getId(), comment.getContent());

        CommentDTO returnedCommentDto = commentService.create(request);

        assertNotNull(returnedCommentDto.id());
        assertEquals(parent.getId(), returnedCommentDto.parentId());
        assertEquals(comment.getContent(), returnedCommentDto.content());
        assertEquals(author.getId(), returnedCommentDto.author().id());
    }


    @Test
    void save_single_ok() {
        Comment comment = new RandomCommentBuilder(author).create();

        Comment returnedComment = commentService.save(comment);

        assertNotNull(returnedComment.getId());
        assertEquals(comment.getContent(), returnedComment.getContent());
        assertEquals(comment.getParent(), returnedComment.getParent());
    }

    @Test
    void save_withChildren_ok() {
        Comment comment = new RandomCommentBuilder(author).withChildren().create();

        Comment returnedComment = commentService.save(comment);

        List<Comment> children = returnedComment.getChildren();
        assertNotNull(returnedComment.getId());
        assertNotNull(children);
        assertNotNull(children.getFirst().getId());
    }

    @Test
    void modify_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        commentService.save(comment);
        CommentDTO commentDTO = CommentMapper.toCommentDTO(comment);

        CommentDTO returnedComment = commentService.modify(comment.getId(), RandomCommentBuilder.getContent());

        assertEquals(commentDTO.id(), returnedComment.id());
        assertEquals(commentDTO.author(), returnedComment.author());
        assertNotEquals(commentDTO.content(), returnedComment.content());
    }

    @Test
    void modify_notAllowed_throwForbidden() {
        mockAuthenticationForbidden();
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        commentService.save(comment);

        assertThrows(ForbiddenException.class,
                () -> commentService.modify(comment.getId(), RandomCommentBuilder.getContent()));
    }


    @Test
    void deleteById_single_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).create();
        commentService.save(comment);

        assertDoesNotThrow(() -> commentService.deleteById(comment.getId()));
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getById(comment.getId()));
    }

    @Test
    void deleteById_withChildren_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        commentService.save(comment);

        assertDoesNotThrow(() -> commentService.deleteById(comment.getId()));
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getById(comment.getId()));
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getById(comment.getChildren().getFirst().getId()));
    }

    @Test
    void deleteById_withParent_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).withParent().create();
        commentService.save(comment);

        assertDoesNotThrow(() -> commentService.deleteById(comment.getId()));
        assertThrows(CommentNotFoundException.class, () -> commentService.getById(comment.getId()));
        assertDoesNotThrow(() -> commentService.getById(comment.getParent().getId()));
    }

    @Test
    void deleteById_notAllowed_throwForbidden() {
        mockAuthenticationForbidden();
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        commentService.save(comment);

        assertThrows(ForbiddenException.class,
                () -> commentService.deleteById(comment.getId()));
    }

    @Test
    void deleteById_throwCommentNotFound() {
        mockAuthentication(author);

        assertThrows(CommentNotFoundException.class,
                () -> commentService.deleteById(NumberUtils.getId()));
    }

    @Test
    void getAllByParentID_ok() {
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        Comment returnedComment = commentService.save(comment);
        List<CommentDTO> children = returnedComment.getChildren()
                .stream()
                .map(CommentMapper::toCommentDTO)
                .toList();

        List<CommentDTO> returnedChildren = commentService.getAllByParentId(comment.getId(),
                        PageRequest.of(0, CHILDREN_COUNT_MAX))
                .getContent();

        assertEquals(children, returnedChildren);
    }

    @Test
    void getAllByParentID_throwParentCommentNotFound() {
        assertThrows(ParentCommentNotFoundException.class,
                () -> commentService.getAllByParentId(NumberUtils.getId(), Pageable.unpaged()));
    }

    @Test
    void getById_ok() {
        Comment comment = new RandomCommentBuilder(author).withChildren().create();
        commentService.save(comment);

        Comment returnedComment = commentService.getById(comment.getId());

        assertEquals(comment.getId(), returnedComment.getId());
        assertEquals(comment.getContent(), returnedComment.getContent());
        assertEquals(comment.getParent(), returnedComment.getParent());
    }

    @Test
    void getById_throwCommentNotFound() {
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getById(NumberUtils.getId()));
    }

    @Test
    void getAllByAuthorId_ok() {
        Comment comment = new RandomCommentBuilder(author).create();
        commentService.save(comment);

        Page<CommentDTO> commentDTOPage = commentService.getAllByAuthorId(author.getId(),
                PageRequest.of(0, 1));

        assertEquals(1, commentDTOPage.getTotalElements());
    }

    @Test
    void getAllByAuthorId_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> commentService.getAllByAuthorId(NumberUtils.getId(), Pageable.unpaged()));
    }

    @Test
    void deleteAllByAuthor_ok() {
        mockAuthentication(author);
        Comment comment = new RandomCommentBuilder(author).create();
        commentService.save(comment);

        commentService.deleteAllByAuthorId(author.getId());
        assertThrows(CommentNotFoundException.class,
                () -> commentService.getById(NumberUtils.getId()));
    }

    @Test
    void deleteAllByAuthor_throwMemberNotFound() {
        assertThrows(MemberNotFoundException.class,
                () -> commentService.deleteAllByAuthorId(NumberUtils.getId()));
    }

    @Test
    void deleteAllByAuthor_throwForbidden() {
        mockAuthenticationForbidden();

        assertThrows(ForbiddenException.class,
                () -> commentService.deleteAllByAuthorId(author.getId()));
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
