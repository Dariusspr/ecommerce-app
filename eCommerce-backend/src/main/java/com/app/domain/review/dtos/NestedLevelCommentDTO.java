package com.app.domain.review.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public record NestedLevelCommentDTO(Long parentId, Long id,
                                    MemberSummaryDTO author, String content,
                                    List<NestedLevelCommentDTO> children, LocalDateTime createdDate,
                                    LocalDateTime lastModifiedDate) {
}
