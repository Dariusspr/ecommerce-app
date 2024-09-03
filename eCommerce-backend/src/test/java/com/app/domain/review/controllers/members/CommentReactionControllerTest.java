package com.app.domain.review.controllers.members;


import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.review.dtos.CommentReactionsInfoDTO;
import com.app.domain.review.dtos.requests.CommentReactionRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.CommentReaction;
import com.app.domain.review.exceptions.CommentNotFoundException;
import com.app.domain.review.exceptions.CommentReactionNotFoundException;
import com.app.domain.review.exceptions.DuplicateCommentReactionException;
import com.app.domain.review.services.CommentReactionService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.review.RandomCommentBuilder;
import com.app.utils.domain.review.RandomCommentReactionBuilder;
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

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = CommentReactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CommentReactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentReactionService commentReactionService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CommentReactionRequest request;
    private CommentReaction reaction;
    private String requestJSON;
    private List<CommentReactionsInfoDTO> reactionsInfoDTO;

    @BeforeAll
    void setup() throws JsonProcessingException {
        Comment comment = new RandomCommentBuilder()
                .withId()
                .create();
        reaction = new RandomCommentReactionBuilder()
                .withCustomComment(comment)
                .withId()
                .create();

        request = new CommentReactionRequest(comment.getId(), reaction.getReactionType());
        requestJSON = StringUtils.toJSON(request);
        reactionsInfoDTO = List.of(new CommentReactionsInfoDTO(reaction.getReactionType(), 1L));
    }

    @Test
    void create_ok() throws Exception {
        given(commentReactionService.create(request)).willReturn(reactionsInfoDTO);

        mockMvc.perform(post(CommentReactionController.BASE_URL)
                        .content(requestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].reactionType", is(reactionsInfoDTO.getFirst().reactionType().toString())))
                .andExpect(jsonPath("$[0].count", is((int) reactionsInfoDTO.getFirst().count())));
    }

    @Test
    void create_notFound() throws Exception {
        doThrow(new CommentNotFoundException()).when(commentReactionService).create(request);

        mockMvc.perform(post(CommentReactionController.BASE_URL)
                        .content(requestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_NOT_FOUND_MESSAGE)));
    }

    @Test
    void create_conflict() throws Exception {
        doThrow(new DuplicateCommentReactionException()).when(commentReactionService).create(request);

        mockMvc.perform(post(CommentReactionController.BASE_URL)
                        .content(requestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.DUPLICATE_COMMENT_REACTION_MESSAGE)));
    }

    @Test
    void modify_ok() throws Exception {
        given(commentReactionService.modify(reaction.getId(), reaction.getReactionType())).willReturn(reactionsInfoDTO);

        mockMvc.perform(put(CommentReactionController.BASE_URL + "/%d/%s".formatted(reaction.getId(), reaction.getReactionType()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].reactionType", is(reactionsInfoDTO.getFirst().reactionType().toString())))
                .andExpect(jsonPath("$[0].count", is((int) reactionsInfoDTO.getFirst().count())));
    }

    @Test
    void modify_notFound() throws Exception {
        doThrow(new CommentReactionNotFoundException()).when(commentReactionService).modify(reaction.getId(), reaction.getReactionType());

        mockMvc.perform(put(CommentReactionController.BASE_URL + "/%d/%s".formatted(reaction.getId(), reaction.getReactionType()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_REACTION_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(commentReactionService).modify(reaction.getId(), reaction.getReactionType());

        mockMvc.perform(put(CommentReactionController.BASE_URL + "/%d/%s".formatted(reaction.getId(), reaction.getReactionType()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void deleteById_ok() throws Exception {
        doNothing().when(commentReactionService).deleteById(reaction.getId());

        mockMvc.perform(delete(CommentReactionController.BASE_URL + "/" + reaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteById_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(commentReactionService).deleteById(reaction.getId());

        mockMvc.perform(delete(CommentReactionController.BASE_URL + "/" + reaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void deleteById_notFound() throws Exception {
        doThrow(new CommentReactionNotFoundException()).when(commentReactionService).deleteById(reaction.getId());

        mockMvc.perform(delete(CommentReactionController.BASE_URL + "/" + reaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_REACTION_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllByCommentId_ok() throws Exception {
        given(commentReactionService.getAllByCommentId(reaction.getId())).willReturn(reactionsInfoDTO);

        mockMvc.perform(get(CommentReactionController.BASE_URL + "/" + reaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].reactionType", is(reactionsInfoDTO.getFirst().reactionType().toString())))
                .andExpect(jsonPath("$[0].count", is((int) reactionsInfoDTO.getFirst().count())));
    }

    @Test
    void getAllByCommentId_notFound() throws Exception {
        given(commentReactionService.getAllByCommentId(reaction.getId())).willReturn(reactionsInfoDTO);
        doThrow(new CommentNotFoundException()).when(commentReactionService).getAllByCommentId(reaction.getId());

        mockMvc.perform(get(CommentReactionController.BASE_URL + "/" + reaction.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_NOT_FOUND_MESSAGE)));
    }
}
