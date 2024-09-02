package com.app.domain.review.services;

import com.app.domain.member.entities.Member;
import com.app.domain.member.services.MemberService;
import com.app.domain.review.dtos.CommentReactionsInfoDTO;
import com.app.domain.review.dtos.requests.CommentReactionRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.CommentReaction;
import com.app.domain.review.enums.ReactionType;
import com.app.domain.review.exceptions.CommentReactionNotFoundException;
import com.app.domain.review.exceptions.DuplicateCommentReactionException;
import com.app.domain.review.repositories.CommentReactionRepository;
import com.app.global.exceptions.ForbiddenException;
import com.app.global.utils.AuthUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentReactionService {

    private final CommentReactionRepository commentReactionRepository;
    private final CommentService commentService;
    private final MemberService memberService;

    public CommentReactionService(CommentReactionRepository commentReactionRepository, CommentService commentService, MemberService memberService) {
        this.commentReactionRepository = commentReactionRepository;
        this.commentService = commentService;
        this.memberService = memberService;
    }

    @Transactional
    public List<CommentReactionsInfoDTO> create(CommentReactionRequest request) {
        Member author = AuthUtils.getAuthenticated();
        Comment comment = commentService.getById(request.commentId());
        CommentReaction reaction = new CommentReaction(author, comment, request.reactionType());
        save(reaction);
        return getAllByComment(comment);
    }

    @Transactional
    public List<CommentReactionsInfoDTO> modify(Long commentReactionId, ReactionType newReactionType) {
        CommentReaction commentReaction = getById(commentReactionId);
        if (AuthUtils.isNotAllowedModifier(commentReaction.getAuthor())) {
            throw new ForbiddenException();
        }
        commentReaction.setReactionType(newReactionType);
        save(commentReaction);
        return getAllByComment(commentReaction.getComment());
    }

    @Transactional
    public CommentReaction save(CommentReaction reaction) {
        try {
            return commentReactionRepository.save(reaction);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCommentReactionException();
        }
    }

    @Transactional
    public void deleteById(Long reactionId) {
        CommentReaction commentReaction = getById(reactionId);
        if (AuthUtils.isNotAllowedModifier(commentReaction.getAuthor())) {
            throw new ForbiddenException();
        }
        commentReactionRepository.deleteById(reactionId);
    }

    public List<CommentReactionsInfoDTO> getAllByCommentId(Long commentId) {
        Comment comment = commentService.getById(commentId);
        return getAllByComment(comment);
    }

    public List<CommentReactionsInfoDTO> getAllByComment(Comment comment) {
        return commentReactionRepository.findAllByComment(comment);
    }

    public long getCountByCommentId(Long commentId) {
        Comment comment = commentService.getById(commentId);
        return commentReactionRepository.findCountByComment(comment);
    }

    public CommentReaction getById(Long reactionId) {
        return commentReactionRepository.findById(reactionId).orElseThrow(CommentReactionNotFoundException::new);
    }

    @Transactional
    public void deleteAllByAuthorId(Long authorId) {
        Member author = memberService.findById(authorId);
        if (AuthUtils.isNotAllowedModifier(author)) {
            throw new ForbiddenException();
        }
        commentReactionRepository.deleteAllByAuthor(author);
    }
}
