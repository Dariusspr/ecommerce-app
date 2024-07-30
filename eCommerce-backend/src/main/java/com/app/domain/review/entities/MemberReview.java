package com.app.domain.review.entities;

import com.app.domain.member.entities.Member;
import jakarta.persistence.*;

@Entity
@Table(name = "member_review")
public class MemberReview extends Review{

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
