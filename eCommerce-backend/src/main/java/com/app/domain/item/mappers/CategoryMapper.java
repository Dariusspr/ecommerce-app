package com.app.domain.item.mappers;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.CategorySaveRequest;
import com.app.domain.item.entities.Category;

import java.util.List;

public class CategoryMapper {

    private CategoryMapper() {}

    public static CategoryDTO toCategoryDTO(Category category) {
        return new CategoryDTO(category.getId(), category.getTitle(), !category.getChildren().isEmpty());
    }

    public static List<CategoryDTO> toCategoryDTO(List<Category> categoryList) {
        return categoryList.stream().map(CategoryMapper::toCategoryDTO).toList();
    }

}
