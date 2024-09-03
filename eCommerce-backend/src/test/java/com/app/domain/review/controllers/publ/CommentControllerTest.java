package com.app.domain.review.controllers.publ;

import com.app.domain.member.entities.Member;
import com.app.domain.member.exceptions.MemberNotFoundException;
import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.dtos.requests.CommentRequest;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.exceptions.ParentCommentNotFoundException;
import com.app.domain.review.mappers.CommentMapper;
import com.app.domain.review.services.CommentService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.member.RandomMemberBuilder;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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
    private Page<CommentDTO> commentDTOPage;
    private final Pageable DEFAULT_PAGEABLE = PageRequest.of(0, 20);

    @BeforeAll
    void setup() throws JsonProcessingException {
        Member member = new RandomMemberBuilder().withId().create();
        comment = new RandomCommentBuilder(member)
                .withId()
                .create();

        request = new CommentRequest(null, comment.getContent());
        requestJSON = StringUtils.toJSON(request);
        commentDTOPage = new PageImpl<>(List.of(CommentMapper.toCommentDTO(comment)), DEFAULT_PAGEABLE, 1);
    }

    @Test
    void getAllByAuthorId_ok() throws Exception {
        given(commentService.getAllByAuthorId(comment.getAuthor().getId(), DEFAULT_PAGEABLE)).willReturn(commentDTOPage);

        mockMvc.perform(get(CommentController.BASE_URL + "/author/" + comment.getAuthor().getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is((int) commentDTOPage.getTotalElements())));
    }

    @Test
    void getAllByAuthorId_notFound() throws Exception {
        doThrow(new MemberNotFoundException()).when(commentService).getAllByAuthorId(comment.getAuthor().getId(), DEFAULT_PAGEABLE);
        mockMvc.perform(get(CommentController.BASE_URL + "/author/" + comment.getAuthor().getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.MEMBER_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getAllByParentId_ok() throws Exception {
        final long parentId = 1;
        given(commentService.getAllByParentId(parentId, DEFAULT_PAGEABLE)).willReturn(commentDTOPage);

        mockMvc.perform(get(CommentController.BASE_URL + "/parent/" + parentId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is((int) commentDTOPage.getTotalElements())));
    }


    @Test
    void getAllByParentId_notFound() throws Exception {
        final long parentId = 1;
        doThrow(new ParentCommentNotFoundException()).when(commentService).getAllByParentId(parentId, DEFAULT_PAGEABLE);
        mockMvc.perform(get(CommentController.BASE_URL + "/parent/" + parentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.PARENT_COMMENT_NOT_FOUND_MESSAGE)));
    }

}
