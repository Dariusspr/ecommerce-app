package com.app.domain.item.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_title", unique = true, nullable = false, length = TITLE_LENGTH)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    protected Category() {}

    public Category(String title) {
        this.title = title;
    }

    public Category(String title, Category parent, List<Category> children) {
        this.title = title;
        this.parent = parent;
        this.children = children;
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
