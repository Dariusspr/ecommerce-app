package com.app.domain.review.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.time.LocalDateTime;
import java.util.Objects;

public record ReviewDTO (Long id, MemberSummaryDTO author,
                         int rating, CommentDTO comment,
                         LocalDateTime createDate, LocalDateTime lastModifiedDate){
    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ReviewDTO reviewDTO)) return false;
        return rating == reviewDTO.rating && Objects.equals(id, reviewDTO.id) && Objects.equals(author, reviewDTO.author) && Objects.equals(comment, reviewDTO.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, rating, comment);
    }
}
