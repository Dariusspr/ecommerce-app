package com.app.domain.review.controllers.members;

import com.app.domain.review.dtos.CommentReactionsInfoDTO;
import com.app.domain.review.dtos.requests.CommentReactionRequest;
import com.app.domain.review.enums.ReactionType;
import com.app.domain.review.services.CommentReactionService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("membersCommentReactionController")
@PreAuthorize("hasAnyRole({'MEMBER', 'ADMIN'})")
@RequestMapping(CommentReactionController.BASE_URL)
public class CommentReactionController {
    public static final String BASE_URL = RestEndpoints.MEMBER_API + "/comment-reactions";

    private final CommentReactionService commentReactionService;

    public CommentReactionController(CommentReactionService commentReactionService) {
        this.commentReactionService = commentReactionService;
    }

    @PostMapping
    public ResponseEntity<List<CommentReactionsInfoDTO>> create(
            @Validated
            @RequestBody
            CommentReactionRequest request) {
        return ResponseEntity.ok(commentReactionService.create(request));
    }

    @PutMapping("/{reactionId}/{newReactionType}")
    public ResponseEntity<List<CommentReactionsInfoDTO>> modify(
            @PathVariable("reactionId")
            @NotNull
            @PositiveOrZero
            Long reactionId,
            @PathVariable("newReactionType")
            @NotNull
            ReactionType newReactionType) {
        return ResponseEntity.ok(commentReactionService.modify(reactionId, newReactionType));
    }

    @DeleteMapping("/{reactionId}")
    public ResponseEntity<?> deleteById(
            @PathVariable("reactionId")
            @NotNull
            @PositiveOrZero
            Long reactionId) {
        commentReactionService.deleteById(reactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<List<CommentReactionsInfoDTO>> getAllByCommentId(
            @PathVariable("commentId")
            @NotNull
            @PositiveOrZero
            Long commentId) {
        return ResponseEntity.ok(commentReactionService.getAllByCommentId(commentId));
    }
}
