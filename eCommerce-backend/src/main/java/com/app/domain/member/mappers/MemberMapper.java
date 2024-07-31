package com.app.domain.member.mappers;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;

import static com.app.global.mappers.MediaMapper.toMediaDTO;

public class MemberMapper {

    private MemberMapper() {
    }

    public static MemberSummaryDTO toMemberSummaryDTO(Member member) {
        return new MemberSummaryDTO(
                member.getId(),
                member.getUsername(),
                toMediaDTO(member.getProfile())
        );
    }
}


