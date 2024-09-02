package com.app.domain.review.repositories;

import com.app.domain.item.entities.Item;
import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.ItemReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemReviewRepository extends JpaRepository<ItemReview, Long> {

    @Query("SELECT ir FROM ItemReview ir WHERE ir.item = :item")
    Page<ItemReview> findAllByItem(@Param("item") Item item, Pageable pageable);

    @Query("SELECT ir FROM ItemReview ir WHERE ir.author = :author")
    Page<ItemReview> findAllByAuthor(@Param("author") Member author, Pageable pageable);

    @Query("SELECT COUNT(ir) FROM ItemReview ir WHERE ir.item = :item")
    long findReviewCountByItem(@Param("item") Item item);

    @Query("SELECT COUNT(ir) FROM ItemReview ir WHERE ir.author = :author")
    long findReviewCountByAuthor(@Param("author") Member author);

    @Query("SELECT COALESCE(AVG(ir.rating), 0.0) FROM ItemReview ir WHERE ir.item = :item")
    float findAverageReviewRatingByItem(@Param("item") Item item);

    @Modifying
    @Query("DELETE FROM ItemReview ir WHERE ir.item = :item")
    void deleteAllByItem(@Param("item") Item item);

    @Modifying
    @Query("DELETE FROM ItemReview ir WHERE ir.author = :author")
    void deleteAllByAuthor(@Param("author") Member author);

}
