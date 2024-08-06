package com.app.domain.item.mappers;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.entities.Category;

public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getTitle(), !category.getChildren().isEmpty());
    }
}
