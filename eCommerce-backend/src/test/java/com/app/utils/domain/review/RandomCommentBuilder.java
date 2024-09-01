package com.app.utils.domain.review;


import com.app.domain.member.entities.Member;
import com.app.domain.review.entities.Comment;
import com.app.utils.domain.member.RandomMemberBuilder;
import com.app.utils.global.NumberUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MIN;


public class RandomCommentBuilder {

    public static final int CHILDREN_COUNT_MAX = 3;
    private static final int CHILDREN_DEPTH_MAX = 4;

    private boolean withId;
    private boolean withParent;
    private boolean withChildren;
    private Integer customChildrenCount;
    private Member customAuthor;
    private boolean withNestedChildren = false;

    private static final int INITIAL_CHILD_DEPTH = 1;

    public RandomCommentBuilder() {
    }

    public RandomCommentBuilder(Member member) {
        customAuthor = member;
    }

    public RandomCommentBuilder withParent() {
        withParent = true;
        return this;
    }

    public RandomCommentBuilder withChildren() {
        withChildren = true;
        return this;
    }

    public RandomCommentBuilder withChildren(Integer customChildrenCount) {
        this.customChildrenCount = customChildrenCount;
        return withChildren();
    }

    public RandomCommentBuilder withNestedChildren() {
        withChildren = true;
        withNestedChildren = true;
        return this;
    }

    public RandomCommentBuilder withId() {
        withId = true;
        return this;
    }

    public List<Comment> create(int count) {
        List<Comment> comments = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            comments.add(create());
        }
        return comments;
    }

    public Comment create() {
        Comment comment = createBasic();
        if (withId) {
            createId(comment);
        }
        if (withParent)
            createParent(comment);
        if (withChildren)
            createChildren(comment);
        if (withNestedChildren)
            createNestedChildren(comment, INITIAL_CHILD_DEPTH);
        return comment;
    }

    private void createNestedChildren(Comment comment, int depth) {
        if (depth >= CHILDREN_DEPTH_MAX) {
            return;
        }
        createChildren(comment);
        comment.getChildren().forEach(c -> createNestedChildren(c, depth + 1));

    }

    private void createChildren(Comment comment) {
        int childrenCount = Objects.requireNonNullElse(
                customChildrenCount,
                NumberUtils.getIntegerInRange(1, CHILDREN_COUNT_MAX));
        for (int i = 0; i < childrenCount; i++) {
            Comment child = createBasic();
            if (withId) {
                createId(child);
            }
            comment.addChild(child);
        }
    }

    private Comment createBasic() {
        Member author = customAuthor;
        if (author == null) {
            author = new RandomMemberBuilder().create();
        }
        return new Comment(author, getContent());
    }

    private void createParent(Comment comment) {
        Comment parent = createBasic();
        parent.addChild(comment);
    }

    private void createId(Comment comment) {
        comment.setId(NumberUtils.getId());
    }

    public static String getContent() {
        return RandomStringUtils.randomAlphanumeric(COMMENT_CONTENT_LENGTH_MIN, COMMENT_CONTENT_LENGTH_MAX);
    }
}
