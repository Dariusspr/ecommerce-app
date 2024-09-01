package com.app.domain.review.repositories;

import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c WHERE c.parent = :parent")
    Page<Comment> findAllByParent(@Param("parent") Comment parent, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.author = :author")
    Page<Comment> findByAuthor(@Param("author") Member author, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.author = :author")
    void deleteAllByAuthor(Member author);
}
