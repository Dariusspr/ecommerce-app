package com.app.domain.member.dtos.responses;

import com.app.domain.member.dtos.MemberSummaryDTO;

public record AuthenticationResponse(MemberSummaryDTO member, String token) {
}
