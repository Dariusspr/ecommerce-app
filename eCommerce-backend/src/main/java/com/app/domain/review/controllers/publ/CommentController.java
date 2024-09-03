package com.app.domain.review.controllers.publ;

import com.app.domain.review.dtos.CommentDTO;
import com.app.domain.review.services.CommentService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("publCommentController")
@RequestMapping(CommentController.BASE_URL)
public class CommentController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/comments";

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<CommentDTO>> getAllByAuthorId(
            @PathVariable("authorId")
            @NotNull
            @PositiveOrZero
            Long authorId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllByAuthorId(authorId, pageable));
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<Page<CommentDTO>> getByParentId(
            @PathVariable("parentId")
            @NotNull
            @PositiveOrZero
            Long parentId,
            Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllByParentId(parentId, pageable));
    }
}