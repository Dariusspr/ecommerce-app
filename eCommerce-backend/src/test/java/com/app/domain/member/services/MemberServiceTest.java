package com.app.domain.member.services;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.global.enums.Gender;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@Tag("Integration test")
public class MemberServiceTest {

    private static final int PAGE_SIZE_1 = 1;


    @Autowired
    private MemberService memberService;


    @Test
    void findById_ok() {
        Member member = new RandomMemberBuilder().create();
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
        Member member = new RandomMemberBuilder().create();
        memberService.save(member);
        final Long id = member.getId();
        assertDoesNotThrow(() -> memberService.findById(id));

        memberService.deleteById(id);

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }

    // DTO methods

    @Test
    void save_ok() {
        Member member = new RandomMemberBuilder().create();

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
        Member member1 = new RandomMemberBuilder().create();
        Member member2 = new RandomMemberBuilder().create();
        member2.setUsername(member1.getUsername());

        memberService.save(member1);
        assertThrows(DataIntegrityViolationException.class, () -> memberService.save(member2));
    }

    @Test
    void save_modify() {
        Member member = new RandomMemberBuilder().create();
        MemberSummaryDTO originalMemberDto = memberService.save(member);
        member.setGender(Gender.MALE);

        MemberSummaryDTO returnedMemberDto = memberService.save(member);

        assertEquals(originalMemberDto, returnedMemberDto);
    }

    @Test
    void findSummaryDtoById_ok() {
        Member member = new RandomMemberBuilder().create();
        MemberSummaryDTO memberDto = memberService.save(member);

        MemberSummaryDTO returnedMemberDto = memberService.findSummaryDtoById(member.getId());

        assertEquals(memberDto, returnedMemberDto);
    }

    @Test
    void findSummaryDtoById_throwMemberNotFound() {
        final Long id = NumberUtils.getId();

        assertThrows(MemberNotFoundException.class, () -> memberService.findById(id));
    }

    @Test
    void findAllSummariesByUsername_single_ok() {
        Member member = new RandomMemberBuilder().create();
        MemberSummaryDTO memberSummaryDTO = memberService.save(member);

        List<MemberSummaryDTO> returnedMemberPage = memberService
                .findAllSummariesByUsername(member.getUsername(), 0, PAGE_SIZE_1)
                .getContent();

        assertEquals(memberSummaryDTO, returnedMemberPage.getFirst());
    }

    @Test
    void findAllSummariesByUsername_multiple_ok() {
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
                .findAllSummariesByUsername(firstUsername, 1, PAGE_SIZE_1)
                .getContent();
        List<MemberSummaryDTO> returnedMemberPage2 = memberService
                .findAllSummariesByUsername(firstUsername, 2, PAGE_SIZE_1)
                .getContent();

        assertFalse(returnedMemberPage1.isEmpty());
        assertFalse(returnedMemberPage2.isEmpty());
    }

    @Test
    void findAllSummariesByUsername_throwMemberNotFound() {
        final String username = RandomMemberBuilder.getUsername();

        assertThrows(MemberNotFoundException.class, () -> memberService
                .findAllSummariesByUsername(username, 1, PAGE_SIZE_1));
    }

}
