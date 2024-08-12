package com.app.domain.member.mappers;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.entities.Member;
import com.app.global.utils.EncryptionUtils;

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

    public static Member toMember(NewMemberRequest request) {
        String encodedPassword = EncryptionUtils.getEncodedPassword(request.password());
        return new Member(request.username(), encodedPassword, request.email());
    }
}


