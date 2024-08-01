package com.app.domain.review.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.time.LocalDateTime;

public record ReviewDTO (Long id, MemberSummaryDTO author,
                         int rating, TopLevelCommentDTO comment,
                         LocalDateTime createDate, LocalDateTime lastModifiedDate){

}
