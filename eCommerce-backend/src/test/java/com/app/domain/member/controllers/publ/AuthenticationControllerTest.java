package com.app.domain.member.controllers.publ;


import com.app.domain.member.dtos.MemberSummaryDTO;
import com.app.domain.member.dtos.requests.AuthenticationRequest;
import com.app.domain.member.dtos.requests.NewMemberRequest;
import com.app.domain.member.dtos.responses.AuthenticationResponse;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.BadMemberCredentialsException;
import com.app.domain.member.exceptions.MemberAlreadyExistsException;
import com.app.domain.member.mappers.MemberMapper;
import com.app.domain.member.services.AuthenticationService;
import com.app.domain.member.services.JwtService;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doNothing;
import static org.mockito.BDDMockito.given;

@Tag("UnitTest")
@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    private static Member member;
    private static NewMemberRequest newMemberRequest;
    private static AuthenticationRequest authRequest;
    private static String authRequestJson;
    private static String newMemberRequestJson;

    @BeforeAll
    static void setupMember() throws JsonProcessingException {
        member = new RandomMemberBuilder().withId().create();
        newMemberRequest = new NewMemberRequest(
                member.getUsername(),
                member.getPassword(),
                member.getEmail());
        newMemberRequestJson = StringUtils.toJSON(newMemberRequest);
        authRequest = new AuthenticationRequest(member.getUsername(), member.getPassword());
        authRequestJson = StringUtils.toJSON(authRequest);
    }

    @Test
    @Order(1)
    void registerNewMember_returnAccepted() throws Exception {
        doNothing().when(authenticationService).registerNewMember(newMemberRequest);

        mockMvc.perform(post(AuthenticationController.BASE_URL + "/register")
                        .content(newMemberRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @Order(2)
    void registerNewMember_returnBadRequest() throws Exception {
        doThrow(new MemberAlreadyExistsException()).when(authenticationService).registerNewMember(any());

        mockMvc.perform(post(AuthenticationController.BASE_URL + "/register")
                        .content(newMemberRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_ALREADY_EXISTS_MESSAGE)));
    }

    @Test
    @Order(3)
    void authenticate_returnOk() throws Exception {
        MemberSummaryDTO memberSummaryDTO = MemberMapper.toMemberSummaryDTO(member);
        AuthenticationResponse response = new AuthenticationResponse(memberSummaryDTO, StringUtils.getText(255));
        given(authenticationService.authenticate(authRequest)).willReturn(response);

        mockMvc.perform(post(AuthenticationController.BASE_URL + "/authenticate")
                        .content(authRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.member.id", is(member.getId())))
                .andExpect(jsonPath("$.member.username", is(member.getUsername())))
                .andExpect(jsonPath("$.member.profile.title", is(member.getProfile().title())))
                .andExpect(jsonPath("$.member.profile.url", is(member.getProfile().url())))
                .andExpect(jsonPath("$.member.profile.format", is(member.getProfile().format().toString())))
                .andExpect(jsonPath("$.token", is(response.token())));
    }

    @Test
    @Order(4)
    void authenticate_returnBadRequest() throws Exception {
        doThrow(new BadMemberCredentialsException()).when(authenticationService).authenticate(authRequest);

        mockMvc.perform(post(AuthenticationController.BASE_URL + "/authenticate")
                        .content(authRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_BAD_CREDENTIALS_MESSAGE)));
    }
}
