package com.app.domain.review.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record TopLevelCommentDTO(Long id, MemberSummaryDTO author,
                                 String content, List<Long> children,
                                 LocalDateTime createdDate, LocalDateTime lastModifiedDate) {
}
