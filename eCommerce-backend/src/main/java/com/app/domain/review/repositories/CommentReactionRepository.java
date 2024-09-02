package com.app.domain.review.repositories;

import com.app.domain.member.entities.Member;
import com.app.domain.review.dtos.CommentReactionsInfoDTO;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.CommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    @Query("SELECT COUNT(cr) FROM CommentReaction cr WHERE cr.comment = :comment")
    long findCountByComment(@Param("comment") Comment comment);

    @Query("""
            SELECT new com.app.domain.review.dtos.CommentReactionsInfoDTO(cr.reactionType, COUNT(cr))\s
            FROM CommentReaction cr\s
            GROUP BY cr.reactionType
            """)
    List<CommentReactionsInfoDTO> findAllByComment(Comment comment);

    @Modifying
    @Query("DELETE FROM CommentReaction cr WHERE cr.author = :author")
    void deleteAllByAuthor(@Param("author") Member author);
}
