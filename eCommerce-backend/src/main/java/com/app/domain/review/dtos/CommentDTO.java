package com.app.domain.review.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public record CommentDTO(Long parentId, Long id,
                         MemberSummaryDTO author, String content,
                         LocalDateTime createdDate, LocalDateTime lastModifiedDate) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDTO that)) return false;
        return Objects.equals(parentId, that.parentId) && Objects.equals(id, that.id) && Objects.equals(author, that.author) && Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId, id, author, content);
    }
}
