package com.app.domain.review.mappers;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.entities.Comment;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDTO toCommentDTO(Comment comment) {
        Comment parent = comment.getParent();
        Member member = comment.getAuthor();
        MemberSummaryDTO memberSummaryDTO = MemberMapper.toMemberSummaryDTO(member);
        return new CommentDTO(
                parent == null ? null : parent.getId(),
                comment.getId(),
                memberSummaryDTO,
                comment.getContent(),
                comment.getCreatedDate(),
                comment.getLastModifiedDate()
        );
    }
}
