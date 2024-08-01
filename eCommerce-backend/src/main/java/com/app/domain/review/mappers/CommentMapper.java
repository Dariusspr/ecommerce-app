package com.app.domain.review.mappers;

import com.app.domain.review.dtos.NestedLevelCommentDTO;
import com.app.domain.review.dtos.TopLevelCommentDTO;
import com.app.domain.review.entities.Comment;

import java.util.List;

import static com.app.domain.member.mappers.MemberMapper.toMemberSummaryDTO;

public class CommentMapper {

    private CommentMapper() {
    }

    public static TopLevelCommentDTO toTopLevelCommentDTO(Comment comment) {
        List<Long> childrenIds = comment.getChildren()
                .stream()
                .map(Comment::getId)
                .toList();
        return new TopLevelCommentDTO(
                comment.getId(),
                toMemberSummaryDTO(comment.getAuthor()),
                comment.getContent(),
                childrenIds,
                comment.getCreatedDate(),
                comment.getLastModifiedDate()
        );
    }

    public static NestedLevelCommentDTO toNestedLevelComment(Comment comment) {
        List<Comment> children = comment.getChildren();
        List<NestedLevelCommentDTO> childrenDTOs = null;
        if (children != null) {
            childrenDTOs = children.stream()
                    .map(CommentMapper::toNestedLevelComment)
                    .toList();
        }
        return new NestedLevelCommentDTO(
                comment.getParent().getId(),
                comment.getId(),
                toMemberSummaryDTO(comment.getAuthor()),
                comment.getContent(),
                childrenDTOs,
                comment.getCreatedDate(),
                comment.getLastModifiedDate()
        );
    }
}
