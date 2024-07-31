package com.app.domain.member.dtos;

import com.app.global.dtos.MediaDTO;

public record MemberSummaryDTO(Long id, String username, MediaDTO profile) {
}
