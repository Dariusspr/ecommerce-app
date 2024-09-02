package com.app.domain.review.entities;

import com.app.domain.member.entities.Member;
import com.app.global.entities.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.COMMENT_CONTENT_LENGTH_MIN;


@Entity
@Table(name = "comment")
public class Comment extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    private Member author;

    @Lob
    @Size(min = COMMENT_CONTENT_LENGTH_MIN, max = COMMENT_CONTENT_LENGTH_MAX)
    @Column(name = "comment_content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "comment_parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReaction> reactions = new ArrayList<>();

    protected Comment() {}

    public Comment(Member author, String content) {
        this.author = author;
        this.content = content;
    }

    public void addChild(Comment child) {
        if (child == null) {
            throw new IllegalArgumentException("'child' is null");
        }
        child.setParent(this);
        children.add(child);
    }

    public void removeChild(Comment child) {
        if (child == null) {
            throw new IllegalArgumentException("'child' is null");
        }
        child.setParent(null);
        children.remove(child);
    }

    public void addReaction(CommentReaction reaction) {
        if (reaction == null) {
            throw new IllegalArgumentException("'reaction' is null");
        }
        reaction.setComment(this);
        reactions.add(reaction);
    }

    public void removeReaction(CommentReaction reaction) {
        if (reaction == null) {
            throw new IllegalArgumentException("'reaction' is null");
        }
        reaction.setComment(null);
        reactions.remove(reaction);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    public List<CommentReaction> getReactions() {
        return reactions;
    }

    public void setReactions(List<CommentReaction> reactions) {
        this.reactions = reactions;
    }
}
