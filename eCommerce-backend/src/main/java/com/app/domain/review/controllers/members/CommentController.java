package com.app.domain.review.controllers.members;

import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.dtos.requests.CommentRequest;
import com.app.domain.review.services.CommentService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MIN;

@RestController("membersCommentController")
@RequestMapping(CommentController.BASE_URL)
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
public class CommentController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/comments";

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentDTO> create(
            @Validated
            @RequestBody
            CommentRequest request) {
        return ResponseEntity.ok(commentService.create(request));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDTO> modify(
            @PathVariable("commentId")
            @NotNull
            @PositiveOrZero
            Long commentId,
            @NotBlank
            @Size(min = COMMENT_CONTENT_LENGTH_MIN, max = COMMENT_CONTENT_LENGTH_MAX)
            @RequestBody
            String newContent) {
        return ResponseEntity.ok(commentService.modify(commentId, newContent));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteById(
            @PathVariable("commentId")
            @NotNull
            @PositiveOrZero
            Long commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.ok().build();
    }
}
