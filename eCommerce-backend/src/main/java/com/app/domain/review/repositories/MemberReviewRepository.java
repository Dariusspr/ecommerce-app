package com.app.domain.review.repositories;

import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.MemberReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberReviewRepository extends JpaRepository<MemberReview, Long> {

    @Query("SELECT mr FROM MemberReview mr WHERE mr.member = :member")
    Page<MemberReview> findAllByMember(@Param("member") Member member, Pageable pageable);

    @Query("SELECT mr FROM MemberReview mr WHERE mr.author = :author")
    Page<MemberReview> findAllByAuthor(@Param("author") Member author, Pageable pageable);

    @Query("SELECT COUNT(mr) FROM MemberReview mr WHERE mr.member = :member")
    long findReviewCountByMember(@Param("member") Member member);

    @Query("SELECT COUNT(mr) FROM MemberReview mr WHERE mr.author = :author")
    long findReviewCountByAuthor(@Param("author") Member author);

    @Query("SELECT COALESCE(AVG(mr.rating), 0.0) FROM MemberReview mr WHERE mr.member = :member")
    float findAverageReviewRatingByMember(@Param("member") Member member);

    @Modifying
    @Query("DELETE FROM MemberReview mr WHERE mr.member = :member")
    void deleteAllByMember(@Param("member") Member member);

    @Modifying
    @Query("DELETE FROM MemberReview mr WHERE mr.author = :author")
    void deleteAllByAuthor(@Param("author") Member author);

}
