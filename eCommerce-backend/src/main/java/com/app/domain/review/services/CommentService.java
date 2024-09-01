package com.app.domain.review.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.dtos.requests.CommentRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.exceptions.CommentNotFoundException;
import com.app.domain.review.exceptions.ParentCommentNotFoundException;
import com.app.domain.review.mappers.CommentMapper;
import com.app.domain.review.repositories.CommentRepository;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberService memberService;

    public CommentService(CommentRepository commentRepository, MemberService memberService) {
        this.commentRepository = commentRepository;
        this.memberService = memberService;
    }

    @Transactional
    public CommentDTO create(CommentRequest request) {
        Member author = AuthUtils.getAuthenticated();
        Comment comment = new Comment(author, request.content());
        commentRepository.save(comment);
        if (request.parentId() != null) {
            try {
                Comment parent = getById(request.parentId());
                parent.addChild(comment);
                save(parent);
            } catch (CommentNotFoundException e) {
                throw new ParentCommentNotFoundException();
            }
        }
        return CommentMapper.toCommentDTO(comment);
    }

    @Transactional
    public CommentDTO modify(Long id, String newContent) {
        Comment comment = getById(id);
        if (AuthUtils.isNotAllowedModifier(comment.getAuthor())) {
            throw new ForbiddenException();
        }
        comment.setContent(newContent);
        return CommentMapper.toCommentDTO(commentRepository.save(comment));
    }

    @Transactional
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteById(Long commentId) {
        Comment comment = getById(commentId);
        if (AuthUtils.isNotAllowedModifier(comment.getAuthor())) {
            throw new ForbiddenException();
        }
        commentRepository.delete(comment);
    }

    public Page<CommentDTO> getAllByParentId(Long parentId, Pageable pageable) {
        try {
            Comment parent = getById(parentId);
            Page<Comment> commentPage = commentRepository.findAllByParent(parent, pageable);
            return commentPage.map(CommentMapper::toCommentDTO);
        } catch (CommentNotFoundException e) {
            throw new ParentCommentNotFoundException();
        }
    }

    public Comment getById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);
    }

    public Page<CommentDTO> getAllByAuthorId(Long authorId, Pageable pageable) {
        Member author = memberService.findById(authorId);
        Page<Comment> commentPage = commentRepository.findByAuthor(author, pageable);
        return commentPage.map(CommentMapper::toCommentDTO);
    }

    @Transactional
    public void deleteAllByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);
        if (AuthUtils.isNotAllowedModifier(author)) {
            throw new ForbiddenException();
        }
        commentRepository.deleteAllByAuthor(author);
    }
}
