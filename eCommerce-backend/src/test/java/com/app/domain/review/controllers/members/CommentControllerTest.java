package com.app.domain.review.controllers.members;


import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.dtos.requests.CommentRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.exceptions.CommentNotFoundException;
import com.app.domain.review.exceptions.ParentCommentNotFoundException;
import com.app.domain.review.mappers.CommentMapper;
import com.app.domain.review.services.CommentService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.review.RandomCommentBuilder;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CommentRequest request;
    private Comment comment;
    private String requestJSON;
    private CommentDTO commentDTO;

    @BeforeAll
    void setup() throws JsonProcessingException {
        comment = new RandomCommentBuilder()
                .withId()
                .create();

        request = new CommentRequest(comment.getParent() == null ? null : comment.getParent().getId(),
                comment.getContent());
        requestJSON = StringUtils.toJSON(request);
        commentDTO = CommentMapper.toCommentDTO(comment);
    }

    @Test
    void create_ok() throws Exception {
        given(commentService.create(request)).willReturn(commentDTO);

        mockMvc.perform(post(CommentController.BASE_URL)
                        .content(requestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parentId", is(request.parentId())))
                .andExpect(jsonPath("$.author", notNullValue()))
                .andExpect(jsonPath("$.content", is(request.content())));
    }

    @Test
    void create_notFound() throws Exception {
        doThrow(new ParentCommentNotFoundException()).when(commentService).create(request);

        mockMvc.perform(post(CommentController.BASE_URL)
                        .content(requestJSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.PARENT_COMMENT_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_ok() throws Exception {
        given(commentService.modify(comment.getId(), comment.getContent())).willReturn(commentDTO);

        mockMvc.perform(put(CommentController.BASE_URL + "/" + comment.getId())
                        .content(comment.getContent()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.parentId", is(request.parentId())))
                .andExpect(jsonPath("$.author", notNullValue()))
                .andExpect(jsonPath("$.content", is(request.content())));
    }

    @Test
    void modify_notFound() throws Exception {
        doThrow(new CommentNotFoundException()).when(commentService).modify(comment.getId(), comment.getContent());

        mockMvc.perform(put(CommentController.BASE_URL + "/" + comment.getId())
                        .content(comment.getContent()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(commentService).modify(comment.getId(), comment.getContent());

        mockMvc.perform(put(CommentController.BASE_URL + "/" + comment.getId())
                        .content(comment.getContent()))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void deleteById_ok() throws Exception {
        doNothing().when(commentService).deleteById(comment.getId());

        mockMvc.perform(delete(CommentController.BASE_URL + "/" + comment.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteById_forbidden() throws Exception {
        doThrow(new ForbiddenException()).when(commentService).deleteById(comment.getId());

        mockMvc.perform(delete(CommentController.BASE_URL + "/" + comment.getId()))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void deleteById_notFound() throws Exception {
        doThrow(new CommentNotFoundException()).when(commentService).deleteById(comment.getId());

        mockMvc.perform(delete(CommentController.BASE_URL + "/" + comment.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.COMMENT_NOT_FOUND_MESSAGE)));
    }
}
