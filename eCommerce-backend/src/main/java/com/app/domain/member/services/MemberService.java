package com.app.domain.member.services;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.repositories.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public void deleteById(Long id) {
        Member member = findById(id);
        memberRepository.delete(member);
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    // DTO methods

    @Transactional
    public MemberSummaryDTO registerNewMember(NewMemberRequest request) {
        if (memberExists(request.username())) {
            throw new MemberAlreadyExistsException();
        }
        Member member = MemberMapper.toMember(request);
        return save(member);
    }

    @Transactional
    public MemberSummaryDTO save(Member member) {
        return MemberMapper.toMemberSummaryDTO(memberRepository.save(member));
    }


    @Transactional(readOnly = true)
    public MemberSummaryDTO findSummaryDtoById(Long id) {
        Member returned = findById(id);
        return MemberMapper.toMemberSummaryDTO(returned);
    }

    public Page<MemberSummaryDTO> findAllSummariesByUsername(String username, Pageable pageable) {
        Page<Member> memberPage = memberRepository.findAllByUsername(username, pageable);
        if (memberPage.isEmpty()) {
            throw new MemberNotFoundException();
        }
        return memberPage.map(MemberMapper::toMemberSummaryDTO);
    }


    private boolean memberExists(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }
}
