package com.app.utils.domain.item;


import com.app.domain.item.entities.Category;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;

import java.util.HashSet;
import java.util.Set;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public class RandomCategoryBuilder {
    private static final int CHILDREN_COUNT_MAX = 3;
    private static final int CHILDREN_DEPTH_MAX = 4;

    private static final Set<String> existingCategoryTitles = new HashSet<>();

    private final Category category;

    public RandomCategoryBuilder() {
        category = new Category(getTitle());

    }

    public RandomCategoryBuilder withParent() {
        createParent(category);
        return this;
    }

    public RandomCategoryBuilder withChildren() {
        createChildren(category);
        return this;
    }

    public RandomCategoryBuilder withNestedChildren() {
        if (category.getChildren() == null) {
            createChildren(category);
        }

        final int currentDepth = 1;
        category.getChildren().forEach(c -> fillNestedChildren(c, currentDepth));
        return this;
    }

    public Category create() {
        return category;
    }

    private void createParent(Category category) {
        Category parent = new Category(getTitle());
        parent.addChild(category);
    }

    private void createChildren(Category category) {
        int childrenCount = NumberUtils.genIntegerInRange(1, CHILDREN_COUNT_MAX);

        for (int i = 0; i < childrenCount; i++) {
            Category child = new Category(getTitle());
            category.addChild(child);
        }
    }

    private void fillNestedChildren(Category category, int depth) {
        if (depth >= CHILDREN_DEPTH_MAX) {
            return;
        }
        createChildren(category);
        category.getChildren().forEach(c -> fillNestedChildren(c, depth + 1));
    }

    private String getTitle() {
        String title = StringUtils.getDistinct(existingCategoryTitles, TITLE_LENGTH_MIN, TITLE_LENGTH_MAX, true, true);
        existingCategoryTitles.add(title);
        return title;
    }
}
