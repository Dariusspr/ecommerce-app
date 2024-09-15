package com.app.domain.member.services;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.DuplicateMemberException;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.repositories.MemberRepository;
import com.app.global.enums.Gender;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MemberServiceTest {

    private static final int PAGE_SIZE_1 = 1;
    private static final Pageable PAGEABLE_0_1 = PageRequest.of(0, PAGE_SIZE_1);

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    private Member member;

    @BeforeEach
    void setup() {
        member = new RandomMemberBuilder().create();
    }

    @AfterEach
    void clear() {
        memberRepository.deleteAll();
    }

    @Test
    void findById_ok() {
        memberService.save(member);

        Member returnedMember = memberService.findById(member.getId());

        assertEquals(member, returnedMember);
    }

    @Test
    void findById_throwMemberNotFound() {
        final Long id = NumberUtils.getId();

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }

    @Test
    void deleteByID_ok() {
        memberService.save(member);
        final Long id = member.getId();
        assertDoesNotThrow(() -> memberService.findById(id));

        memberService.deleteById(id);

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }


    @Test
    void save_ok() {
        MemberSummaryDTO returnedMemberDto = memberService.save(member);

        assertNotNull(returnedMemberDto.id());
        assertEquals(MemberMapper.toMemberSummaryDTO(member), returnedMemberDto);
    }

    @Test
    void save_throwConstraintViolationException() {
        Member member = new RandomMemberBuilder().create();
        member.setUsername(null);

        assertThrows(ConstraintViolationException.class, () -> memberService.save(member));
    }

    @Test
    void save_throwDataIntegrityViolationException() {
        Member member2 = new RandomMemberBuilder().create();
        member2.setUsername(member.getUsername());

        memberService.save(member);
        assertThrows(DuplicateMemberException.class, () -> memberService.save(member2));
    }

    @Test
    void save_modify() {
        MemberSummaryDTO originalMemberDto = memberService.save(member);
        member.setGender(Gender.MALE);

        MemberSummaryDTO returnedMemberDto = memberService.save(member);

        assertEquals(originalMemberDto, returnedMemberDto);
    }

    @Test
    void findSummaryById_ok() {
        MemberSummaryDTO memberDto = memberService.save(member);

        MemberSummaryDTO returnedMemberDto = memberService.findSummaryById(member.getId());

        assertEquals(memberDto, returnedMemberDto);
    }

    @Test
    void findSummaryById_throwMemberNotFound() {
        final Long id = NumberUtils.getId();

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }

    @Test
    void findAllSummariesByUsername_single_ok() {
        MemberSummaryDTO memberSummaryDTO = memberService.save(member);

        List<MemberSummaryDTO> returnedMemberPage = memberService
                .findAllSummariesByUsername(member.getUsername(), PAGEABLE_0_1)
                .getContent();

        assertEquals(memberSummaryDTO, returnedMemberPage.getFirst());
    }

    @Test
    void findAllSummariesByUsername_multiplePages_ok() {
        final int memberCountToTest = 5;
        RandomMemberBuilder builder = new RandomMemberBuilder();
        List<Member> members = builder.create(memberCountToTest);
        String firstUsername = members.getFirst().getUsername();
        String modifiedUsername1 = firstUsername + StringUtils.getText(1);
        String modifiedUsername2 = StringUtils.getText(1) + members.getFirst().getUsername();
        members.get(2).setUsername(modifiedUsername1);
        members.get(3).setUsername(modifiedUsername2);
        members.forEach(memberService::save);

        List<MemberSummaryDTO> returnedMemberPage1 = memberService
                .findAllSummariesByUsername(firstUsername, PAGEABLE_0_1)
                .getContent();
        List<MemberSummaryDTO> returnedMemberPage2 = memberService
                .findAllSummariesByUsername(firstUsername, PageRequest.of(1, PAGE_SIZE_1))
                .getContent();
        List<MemberSummaryDTO> returnedMemberPage3 = memberService
                .findAllSummariesByUsername(firstUsername, PageRequest.of(2, PAGE_SIZE_1))
                .getContent();

        assertFalse(returnedMemberPage1.isEmpty());
        assertFalse(returnedMemberPage2.isEmpty());
        assertFalse(returnedMemberPage3.isEmpty());
    }

    @Test
    void findAllSummariesByUsername_throwMemberNotFound() {
        final String username = member.getUsername();

        assertThrows(MemberNotFoundException.class, () -> memberService
                .findAllSummariesByUsername(username, PAGEABLE_0_1));
    }

}
