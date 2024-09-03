package com.app.domain.review.controllers.members;

import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.member.entities.Member;
import com.app.domain.review.dtos.ReviewDTO;
import com.app.domain.review.dtos.requests.ItemReviewRequest;
import com.app.domain.review.dtos.requests.ModifyReviewRequest;
import com.app.domain.review.entities.ItemReview;
import com.app.domain.review.exceptions.DuplicateReviewException;
import com.app.domain.review.exceptions.ReviewNotFoundException;
import com.app.domain.review.mappers.ReviewMapper;
import com.app.domain.review.services.ItemReviewService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.global.exceptions.ForbiddenException;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private ItemReviewRequest reviewRequest;
    private ModifyReviewRequest modifyReviewRequest;
    private String reviewRequestJSON;
    private String modifyRequestJSON;
    private ReviewDTO reviewDTO;

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

        reviewRequest = new ItemReviewRequest(item.getId(),
                review.getRating(),
                review.getComment().getContent());
        modifyReviewRequest = new ModifyReviewRequest(review.getRating(), review.getComment().getContent());
        reviewRequestJSON = StringUtils.toJSON(reviewRequest);
        modifyRequestJSON = StringUtils.toJSON(modifyReviewRequest);
        reviewDTO = ReviewMapper.toReviewDTO(review);
    }

    @Test
    void create_ok() throws Exception {
        given(itemReviewService.create(reviewRequest)).willReturn(reviewDTO);

        mockMvc.perform(post(ItemReviewController.BASE_URL)
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(reviewDTO.id())))
                .andExpect(jsonPath("$.author", notNullValue()))
                .andExpect(jsonPath("$.rating", is(reviewDTO.rating())))
                .andExpect(jsonPath("$.comment", notNullValue()));
    }

    @Test
    void create_notFound() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemReviewService).create(reviewRequest);

        mockMvc.perform(post(ItemReviewController.BASE_URL)
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void create_conflict() throws Exception {
        doThrow(new DuplicateReviewException()).when(itemReviewService).create(reviewRequest);

        mockMvc.perform(post(ItemReviewController.BASE_URL)
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.DUPLICATE_REVIEW_MESSAGE)));
    }

    @Test
    void deleteById_ok() throws Exception {
        doNothing().when(itemReviewService).deleteById(reviewDTO.id());
        mockMvc.perform(delete(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    void deleteById_notFound() throws Exception {
        doThrow(new ReviewNotFoundException()).when(itemReviewService).deleteById(reviewDTO.id());
        mockMvc.perform(delete(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.REVIEW_NOT_FOUND_MESSAGE)));
    }

    @Test
    void deleteById_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(itemReviewService).deleteById(reviewDTO.id());
        mockMvc.perform(delete(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(reviewRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void modify_notFound() throws Exception {
        doThrow(new ReviewNotFoundException()).when(itemReviewService).modify(reviewDTO.id(), modifyReviewRequest);

        mockMvc.perform(put(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(modifyRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.REVIEW_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_ok() throws Exception {
        given(itemReviewService.modify(reviewDTO.id(), modifyReviewRequest)).willReturn(reviewDTO);

        mockMvc.perform(put(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(modifyRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(reviewDTO.id())))
                .andExpect(jsonPath("$.author", notNullValue()))
                .andExpect(jsonPath("$.rating", is(reviewDTO.rating())))
                .andExpect(jsonPath("$.comment", notNullValue()));
    }

    @Test
    void modify_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(itemReviewService).modify(reviewDTO.id(), modifyReviewRequest);

        mockMvc.perform(put(ItemReviewController.BASE_URL + "/" + reviewDTO.id())
                        .content(modifyRequestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }
}