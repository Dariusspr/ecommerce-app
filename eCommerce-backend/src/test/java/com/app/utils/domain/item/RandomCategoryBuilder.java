package com.app.utils.domain.item;


import com.app.domain.item.entities.Category;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;

import java.util.*;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public class RandomCategoryBuilder {
    private static final int CHILDREN_COUNT_MAX = 3;
    private static final int CHILDREN_DEPTH_MAX = 4;

    private static final Set<String> existingCategoryTitles = new HashSet<>();
    private static final Set<Long> existingCategoryIds = new HashSet<>();

    private boolean withParent;
    private boolean withChildren = false;
    private Integer customChildrenCount;
    private boolean withNestedChildren = false;
    private boolean withId = false;


    private static final int INITIAL_CHILD_DEPTH = 1;

    public RandomCategoryBuilder() {
    }

    public RandomCategoryBuilder withId() {
        withId = true;
        return this;
    }

    public RandomCategoryBuilder withParent() {
        withParent = true;
        return this;
    }

    public RandomCategoryBuilder withChildren() {
        withChildren = true;
        return this;
    }

    public RandomCategoryBuilder withChildren(Integer customChildrenCount) {
        this.customChildrenCount = customChildrenCount;
        return withChildren();
    }

    public RandomCategoryBuilder withNestedChildren() {
        withChildren = true;
        withNestedChildren = true;
        return this;
    }

    public Category create() {
        Category category = new Category(getTitle());
        if (withId) {
            createId(category);
        }
        if (withParent)
            createParent(category);
        if (withChildren)
            createChildren(category);
        if (withNestedChildren)
            createNestedChildren(category, INITIAL_CHILD_DEPTH);
        return category;
    }

    private void createId(Category category) {
        Long id = NumberUtils.getDistinctId(existingCategoryIds);
        existingCategoryIds.add(id);
        category.setId(id);
    }

    public List<Category> create(int count) {
        List<Category> categories = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            categories.add(create());
        }
        return categories;
    }

    private void createParent(Category category) {
        Category parent = new Category(getTitle());
        parent.addChild(category);
    }

    private void createChildren(Category category) {
        int childrenCount = Objects.requireNonNullElse(
                customChildrenCount,
                NumberUtils.getIntegerInRange(1, CHILDREN_COUNT_MAX));

        for (int i = 0; i < childrenCount; i++) {
            Category child = new Category(getTitle());
            if (withId) {
                createId(child);
            }
            category.addChild(child);
        }
    }

    private void createNestedChildren(Category category, int depth) {
        if (depth >= CHILDREN_DEPTH_MAX) {
            return;
        }
        createChildren(category);
        category.getChildren().forEach(c -> createNestedChildren(c, depth + 1));
    }

    public static String getTitle() {
        String title = StringUtils.getDistinct(existingCategoryTitles, TITLE_LENGTH_MIN + 5, TITLE_LENGTH_MAX - 5, true, true);
        existingCategoryTitles.add(title);
        return title;
    }

    public static Category chain(List<Category> categories) {
        Category parent = categories.getFirst();
        categories.removeFirst();
        Category current = parent;
        for (Category category : categories) {
            current.addChild(category);
            current = category;
        }
        return parent;
    }
}
