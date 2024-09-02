package com.app.domain.review.entities;

import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.base.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "member_review", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "review_author_id"})
})
public class MemberReview extends Review {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id")
    private Member member;

    protected MemberReview() {}

    public MemberReview(Member author, int rating, Comment comment, Member member) {
        super(author, rating, comment);
        this.member = member;
    }

    // AUTO GENERATED

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }
}
