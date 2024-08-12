package com.app.domain.member.services;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.repositories.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Transactional
    public MemberSummaryDTO save(Member member) {
        return MemberMapper.toMemberSummaryDTO(memberRepository.save(member));
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

    public Page<MemberSummaryDTO> findAllSummariesByUsername(String username, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Member> memberPage = memberRepository.findAllByUsername(username, pageable);
        if (memberPage.isEmpty()) {
            throw new MemberNotFoundException();
        }
        return memberPage.map(MemberMapper::toMemberSummaryDTO);
    }

    // DTO methods

    @Transactional
    public MemberSummaryDTO findSummaryDtoById(Long id) {
        Member returned = findById(id);
        return MemberMapper.toMemberSummaryDTO(returned);
    }



    private boolean memberExists(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }
}
