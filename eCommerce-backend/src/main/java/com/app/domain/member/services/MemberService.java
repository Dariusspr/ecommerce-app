package com.app.domain.member.services;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.DuplicateMemberException;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.repositories.MemberRepository;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public MemberSummaryDTO save(Member member) {
        try {
            return MemberMapper.toMemberSummaryDTO(memberRepository.saveAndFlush(member));
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateMemberException();
        }
    }

    @Transactional(readOnly = true)
    public MemberSummaryDTO findSummaryById(Long id) {
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

    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username).orElseThrow(MemberNotFoundException::new);
    }

    public boolean memberExists(String username) {
        return memberRepository.findByUsername(username).isPresent();
    }
}
