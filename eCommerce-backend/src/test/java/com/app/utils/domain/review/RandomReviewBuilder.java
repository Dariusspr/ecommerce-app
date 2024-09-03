package com.app.utils.domain.review;

import com.app.domain.item.entities.Item;
import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.ItemReview;
import com.app.domain.review.entities.MemberReview;
import com.app.domain.review.entities.base.Review;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;

import java.util.Objects;

import static com.app.global.constants.UserInputConstants.RATING_MAX;
import static com.app.global.constants.UserInputConstants.RATING_MIN;

public class RandomReviewBuilder {

    private Member customAuthor;
    private boolean withId;

    public RandomReviewBuilder() {

    }

    public RandomReviewBuilder withCustomAuthor(Member author) {
        this.customAuthor = author;
        return this;
    }

    public RandomReviewBuilder withId() {
        withId = true;
        return this;
    }

    public Review create(Object target) {
        Member author = Objects.requireNonNullElseGet(customAuthor,
                () -> new RandomMemberBuilder().create());

        Review review;
        if (target instanceof Item item) {
            review = new ItemReview(author, getRating(), getCommentTemplate(author), item);
        } else if (target instanceof Member member) {
            review = new MemberReview(author, getRating(), getCommentTemplate(author), member);
        } else {
            throw new IllegalArgumentException("Unsupported review target.");
        }

        if (withId)
            review.setId(NumberUtils.getId());

        return review;
    }

    public static int getRating() {
        return NumberUtils.getIntegerInRange(RATING_MIN, RATING_MAX);
    }

    public Comment getCommentTemplate(Member author) {
        return new Comment(author, "<EMPTY COMMENT>");
    }


}
