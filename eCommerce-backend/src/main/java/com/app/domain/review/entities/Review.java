package com.app.domain.review.entities;

import com.app.domain.member.entities.Member;
import com.app.global.entities.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import static com.app.global.constants.UserInputConstants.RATING_MAX;
import static com.app.global.constants.UserInputConstants.RATING_MIN;

@MappedSuperclass
public abstract class Review extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "review_author", nullable = false)
    private Member author;

    @Min(RATING_MIN)
    @Max(RATING_MAX)
    @Column(name = "review_rating", nullable = false)
    private int rating;

    @OneToOne(optional = false)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    protected Review() {}

    protected Review(Member author, int rating, Comment comment) {
        this.author = author;
        this.rating = rating;
        this.comment = comment;
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getAuthor() {
        return author;
    }

    public void setAuthor(Member author) {
        this.author = author;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }
}
