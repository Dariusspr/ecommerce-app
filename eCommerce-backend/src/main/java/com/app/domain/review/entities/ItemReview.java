package com.app.domain.review.entities;

import com.app.domain.item.entities.Item;
import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.base.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "item_review", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"item_id", "review_author_id"})
})
public class ItemReview extends Review {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id")
    private Item item;

    protected ItemReview() {}

    public ItemReview(Member author, int rating, Comment comment, Item item) {
        super(author, rating, comment);
        this.item = item;
    }

    // AUTO GENERATED

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
