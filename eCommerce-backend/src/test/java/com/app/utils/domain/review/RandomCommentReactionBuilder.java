package com.app.utils.domain.review;

import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.Comment;
import com.app.domain.review.entities.CommentReaction;
import com.app.domain.review.enums.ReactionType;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RandomCommentReactionBuilder {

    private Member customAuthor;
    private Comment customComment;
    private ReactionType customReactionType;

    public RandomCommentReactionBuilder() {
    }

    public RandomCommentReactionBuilder withCustomAuthor(Member customAuthor) {
        this.customAuthor = customAuthor;
        return this;
    }

    public RandomCommentReactionBuilder withCustomComment(Comment customComment) {
        this.customComment = customComment;
        return this;
    }

    public RandomCommentReactionBuilder withCustomReactionType(ReactionType customReactionType) {
        this.customReactionType = customReactionType;
        return this;
    }

    public CommentReaction create() {
        return createBasic();
    }

    public List<CommentReaction> create(int count) {
        List<CommentReaction> commentReactionList = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            commentReactionList.add(create());
        }
        return commentReactionList;
    }

    private CommentReaction createBasic() {
        Member author = Objects.requireNonNullElseGet(customAuthor,
                () -> new RandomMemberBuilder().create());
        Comment comment = Objects.requireNonNullElseGet(customComment,
                () -> new RandomCommentBuilder().create());
        ReactionType reactionType = Objects.requireNonNullElseGet(customReactionType,
                RandomCommentReactionBuilder::getRandomReactionType);

        return new CommentReaction(author, comment, reactionType);
    }

    public static ReactionType getRandomReactionType() {
        ReactionType[] reactionTypes = ReactionType.values();
        return reactionTypes[NumberUtils.getIntegerInRange(0, reactionTypes.length - 1)];
    }
}
