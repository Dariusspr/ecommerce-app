package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.services.JwtService;
import com.app.domain.member.services.MemberService;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.member.RandomMemberBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {
    private static final int PAGE_NUMBER_0 = 0;
    private static final int PAGE_SIZE = 20;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private JwtService jwtService;

    private final Pageable pageable = PageRequest.of(PAGE_NUMBER_0, PAGE_SIZE);

    @Test
    void getMemberById_returnOk() throws Exception {
        final Member member = new RandomMemberBuilder()
                .withId()
                .create();
        final MemberSummaryDTO memberSummaryDTO = MemberMapper.toMemberSummaryDTO(member);
        given(memberService.findSummaryDtoById(member.getId()))
                .willReturn(memberSummaryDTO);

        mockMvc.perform(get(MemberController.BASE_URL + "/" + member.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(member.getId())))
                .andExpect(jsonPath("$.username", is(member.getUsername())))
                .andExpect(jsonPath("$.profile.title", is(member.getProfile().title())))
                .andExpect(jsonPath("$.profile.url", is(member.getProfile().url())))
                .andExpect(jsonPath("$.profile.format", is(member.getProfile().format().toString())));
    }

    @Test
    void getMemberById_returnBadRequest() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).findSummaryDtoById(anyLong());

        mockMvc.perform(get(MemberController.BASE_URL + "/" + anyLong())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getMembersByUsernames_returnOk() throws Exception {
        final int memberCount = 2;
        final List<Member> memberList = new RandomMemberBuilder().withId().create(memberCount);
        final List<MemberSummaryDTO> memberSummaryDTOS = memberList.stream()
                .map(MemberMapper::toMemberSummaryDTO)
                .toList();
        final Page<MemberSummaryDTO> memberSummaryDTOPage = new PageImpl<>(
                memberSummaryDTOS,
                PageRequest.of(PAGE_NUMBER_0, PAGE_SIZE),
                memberSummaryDTOS.size());
        String username = RandomMemberBuilder.getUsername();
        given(memberService.findAllSummariesByUsername(username, pageable))
                .willReturn(memberSummaryDTOPage);

        mockMvc.perform(get(MemberController.BASE_URL + "/username/" + username)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(memberCount)));
    }

    @Test
    void getMembersByUsernames_noPageNumber_returnOk() throws Exception {
        final int memberCount = 2;
        final List<Member> memberList = new RandomMemberBuilder().withId().create(memberCount);
        final List<MemberSummaryDTO> memberSummaryDTOS = memberList.stream()
                .map(MemberMapper::toMemberSummaryDTO)
                .toList();
        final Page<MemberSummaryDTO> memberSummaryDTOPage = new PageImpl<>(
                memberSummaryDTOS,
                PageRequest.of(PAGE_NUMBER_0, PAGE_SIZE),
                memberSummaryDTOS.size());
        String username = RandomMemberBuilder.getUsername();
        given(memberService.findAllSummariesByUsername(username, pageable))
                .willReturn(memberSummaryDTOPage);

        mockMvc.perform(get(MemberController.BASE_URL + "/username/" + username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(memberCount)));
    }

    @Test
    void getMembersByUsernames_returnBadRequest() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).findAllSummariesByUsername(anyString(), any());

        mockMvc.perform(get(MemberController.BASE_URL + "/username/" + RandomMemberBuilder.getUsername())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }
}
