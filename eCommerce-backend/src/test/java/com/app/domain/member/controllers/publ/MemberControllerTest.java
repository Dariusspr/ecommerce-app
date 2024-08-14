package com.app.domain.member.controllers.publ;

import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.services.MemberService;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("Unit test")
@WebMvcTest(controllers = MemberController.class)
@ExtendWith(MockitoExtension.class)
public class MemberControllerTest {
    private final int PAGE_NUMBER_0 = 0;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

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
                PageRequest.of(PAGE_NUMBER_0, MemberController.PAGE_SIZE),
                memberSummaryDTOS.size());
        given(memberService.findAllSummariesByUsername(" ", PAGE_NUMBER_0, MemberController.PAGE_SIZE))
                .willReturn(memberSummaryDTOPage);

        mockMvc.perform(get(MemberController.BASE_URL + "/username/ ")
                        .param("pageNumber", String.valueOf(PAGE_NUMBER_0))
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
                PageRequest.of(PAGE_NUMBER_0, MemberController.PAGE_SIZE),
                memberSummaryDTOS.size());
        given(memberService.findAllSummariesByUsername(" ", PAGE_NUMBER_0, MemberController.PAGE_SIZE))
                .willReturn(memberSummaryDTOPage);

        mockMvc.perform(get(MemberController.BASE_URL + "/username/ ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(memberCount)));
    }

    @Test
    void getMembersByUsernames_returnBadRequest() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberService).findAllSummariesByUsername(anyString(), anyInt(), anyInt());

        mockMvc.perform(get(MemberController.BASE_URL + "/username/ ")
                        .param("pageNumber", String.valueOf(PAGE_NUMBER_0))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void registerNewMember_returnOk() throws Exception {
        final Member member = new RandomMemberBuilder().withId().create();
        final NewMemberRequest request = new NewMemberRequest(member.getUsername(),
                member.getPassword(),
                member.getEmail());
        final MemberSummaryDTO memberSummaryDTO = MemberMapper.toMemberSummaryDTO(member);
        final String requestJson = StringUtils.toJSON(request);
        given(memberService.registerNewMember(request)).willReturn(memberSummaryDTO);

        mockMvc.perform(post(MemberController.BASE_URL)
                        .content(requestJson)
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
    void registerNewMember_returnBadRequest() throws Exception {
        final NewMemberRequest request = new NewMemberRequest(RandomMemberBuilder.getUsername(),
                RandomMemberBuilder.getPassword(),
                RandomMemberBuilder.getEmail());
        final String requestJson = StringUtils.toJSON(request);
        doThrow(new MemberAlreadyExistsException()).when(memberService).registerNewMember(any());

        mockMvc.perform(post(MemberController.BASE_URL)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_ALREADY_EXISTS_MESSAGE)));
    }
}
