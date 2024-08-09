package com.app.domain.item.dtos.requests;

public record NewCategoryRequest(Long parentId, String title) {
}
