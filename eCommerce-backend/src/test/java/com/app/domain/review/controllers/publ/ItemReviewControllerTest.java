package com.app.domain.review.controllers.publ;

import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.ItemReviewRequest;
import com.app.domain.review.entities.ItemReview;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.services.ItemReviewService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.item.RandomItemBuilder;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = ItemReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ItemReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemReviewService itemReviewService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private ItemReviewRequest request;
    private String requestJSON;
    private ReviewDTO reviewDTO;
    private final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 20);
    private Page<ReviewDTO> returnPage;

    @BeforeAll
    void setup() throws JsonProcessingException {
        Item item = new RandomItemBuilder()
                .withId()
                .create();
        Member author = new RandomMemberBuilder().withId().create();
        ItemReview review = (ItemReview) new RandomReviewBuilder()
                .withCustomAuthor(author)
                .withId()
                .create(item);

        request = new ItemReviewRequest(item.getId(),
                review.getRating(),
                review.getComment().getContent());
        requestJSON = StringUtils.toJSON(request);
        reviewDTO = ReviewMapper.toReviewDTO(review);
        returnPage = new PageImpl<>(List.of(reviewDTO), DEFAULT_PAGEABLE, 1L);
    }

    @Test
    void getAllByItemId_ok() throws Exception {
        given(itemReviewService.getAllByItemId(request.itemId(), DEFAULT_PAGEABLE))
                .willReturn(returnPage);

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(reviewDTO.id().intValue())))
                .andExpect(jsonPath("$.content[0].author", notNullValue()))
                .andExpect(jsonPath("$.content[0].rating", is(reviewDTO.rating())))
                .andExpect(jsonPath("$.content[0].comment", notNullValue()));
    }

    @Test
    void getAllByItemId_notFound() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemReviewService).getAllByItemId(request.itemId(), DEFAULT_PAGEABLE);

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllByAuthorId_ok() throws Exception {
        given(itemReviewService.getAllByAuthorId(reviewDTO.author().id(), DEFAULT_PAGEABLE))
                .willReturn(returnPage);

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/author/" + reviewDTO.author().id())
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
        doThrow(new MemberNotFoundException()).when(itemReviewService).getAllByAuthorId(reviewDTO.author().id(), DEFAULT_PAGEABLE);

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/author/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }


    @Test
    void getCountByItemId_ok() throws Exception {
        given(itemReviewService.getReviewCountByItemId(request.itemId()))
                .willReturn(1L);

        MvcResult result = mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/count/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Long returnedCount = Long.parseLong(result.getResponse().getContentAsString());
        assertEquals(1L, returnedCount);
    }

    @Test
    void getCountByItemId_notFound() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemReviewService).getReviewCountByItemId(request.itemId());

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/count/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getCountByAuthorId_ok() throws Exception {
        given(itemReviewService.getReviewCountByAuthorId(reviewDTO.author().id()))
                .willReturn(1L);

        MvcResult result = mockMvc.perform(get(ItemReviewController.BASE_URL + "/author/count/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Long returnedCount = Long.parseLong(result.getResponse().getContentAsString());
        assertEquals(1L, returnedCount);
    }

    @Test
    void getCountByAuthorId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(itemReviewService).getReviewCountByAuthorId(reviewDTO.author().id());

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/author/count/" + reviewDTO.author().id())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getRatingByItemId() throws Exception {
        given(itemReviewService.getAverageReviewRatingByItemId(request.itemId()))
                .willReturn((float) request.rating());

        MvcResult result = mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/rating/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Float returnedRating = Float.parseFloat(result.getResponse().getContentAsString());
        assertEquals(request.rating(), returnedRating);
    }

    @Test
    void getRatingByItemId_notFound() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemReviewService).getAverageReviewRatingByItemId(request.itemId());

        mockMvc.perform(get(ItemReviewController.BASE_URL + "/item/rating/" + request.itemId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }
}
