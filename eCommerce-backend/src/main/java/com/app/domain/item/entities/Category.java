package com.app.domain.item.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.app.global.constants.UserInputConstants.*;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @NotBlank
    @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
    @Column(name = "category_title", unique = true, nullable = false, length = TITLE_LENGTH_MAX)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                    CascadeType.REFRESH, CascadeType.PERSIST})
    @JoinColumn(name = "category_parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    protected Category() {
    }

    public Category(String title) {
        this.title = title;
    }

    public void addChild(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("'category' is null");
        }
        category.setParent(this);
        children.add(category);
    }

    public void removeChild(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("'category' is null");
        }
        category.setParent(null);
        children.remove(category);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Category category)) return false;
        return Objects.equals(getTitle(), category.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    // AUTO GENERATED

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }
}
