package com.app.domain.review.controllers.publ;

import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.MemberReviewRequest;
import com.app.domain.review.entities.MemberReview;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.services.MemberReviewService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.domain.review.RandomReviewBuilder;
import com.app.utils.global.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class MemberReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberReviewService memberReviewController;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private MemberReviewRequest request;
    private String requestJSON;
    private ReviewDTO reviewDTO;
    private final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 20);
    private Page<ReviewDTO> returnPage;

    @BeforeAll
    void setup() throws JsonProcessingException {
        Member member = new RandomMemberBuilder()
                .withId()
                .create();
        Member author = new RandomMemberBuilder().withId().create();
        MemberReview review = (MemberReview) new RandomReviewBuilder()
                .withCustomAuthor(author)
                .withId()
                .create(member);

        request = new MemberReviewRequest(member.getId(),
                review.getRating(),
                review.getComment().getContent());
        requestJSON = StringUtils.toJSON(request);
        reviewDTO = ReviewMapper.toReviewDTO(review);
        returnPage = new PageImpl<>(List.of(reviewDTO), DEFAULT_PAGEABLE, 1L);
    }

    @Test
    void getAllByMemberId_ok() throws Exception {
        given(memberReviewController.getAllByMemberId(request.memberId(), DEFAULT_PAGEABLE))
                .willReturn(returnPage);

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(reviewDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].author", notNullValue()))
                .andExpect(jsonPath("$.content[0].rating", is(reviewDTO.rating())))
                .andExpect(jsonPath("$.content[0].comment", notNullValue()));
    }

    @Test
    void getAllByMemberId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberReviewController).getAllByMemberId(request.memberId(), DEFAULT_PAGEABLE);

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllByAuthorId_ok() throws Exception {
        given(memberReviewController.getAllByAuthorId(reviewDTO.author().id(), DEFAULT_PAGEABLE))
                .willReturn(returnPage);

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/author/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(reviewDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].author", notNullValue()))
                .andExpect(jsonPath("$.content[0].rating", is(reviewDTO.rating())))
                .andExpect(jsonPath("$.content[0].comment", notNullValue()));
    }

    @Test
    void getAllByAuthorId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberReviewController).getAllByAuthorId(reviewDTO.author().id(), DEFAULT_PAGEABLE);

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/author/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }


    @Test
    void getCountByMemberId_ok() throws Exception {
        given(memberReviewController.getReviewCountByMemberId(request.memberId()))
                .willReturn(1L);

        MvcResult result = mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/count/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Long returnedCount = Long.parseLong(result.getResponse().getContentAsString());
        assertEquals(1L, returnedCount);
    }

    @Test
    void getCountByMemberId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberReviewController).getReviewCountByMemberId(request.memberId());

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/count/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getCountByAuthorId_ok() throws Exception {
        given(memberReviewController.getReviewCountByAuthorId(reviewDTO.author().id()))
                .willReturn(1L);

        MvcResult result = mockMvc.perform(get(MemberReviewController.BASE_URL + "/author/count/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long returnedCount = Long.parseLong(result.getResponse().getContentAsString());
        assertEquals(1L, returnedCount);
    }

    @Test
    void getCountByAuthorId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberReviewController).getReviewCountByAuthorId(reviewDTO.author().id());

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/author/count/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getRatingByMemberId() throws Exception {
        given(memberReviewController.getAverageReviewRatingByMemberId(request.memberId()))
                .willReturn((float) request.rating());

        MvcResult result = mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/rating/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Float returnedRating = Float.parseFloat(result.getResponse().getContentAsString());
        assertEquals(request.rating(), returnedRating);
    }

    @Test
    void getRatingByMemberId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(memberReviewController).getAverageReviewRatingByMemberId(request.memberId());

        mockMvc.perform(get(MemberReviewController.BASE_URL + "/member/rating/" + request.memberId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }
}