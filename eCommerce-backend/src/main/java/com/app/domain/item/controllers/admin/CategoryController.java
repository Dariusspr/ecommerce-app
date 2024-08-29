package com.app.domain.item.controllers.admin;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("adminCategoryController")
@RequestMapping(CategoryController.BASE_URL)
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {
    public static final String BASE_URL = RestEndpoints.ADMIN_API + "/categories";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> addNewCategory(
            @Validated
            @RequestBody
            NewCategoryRequest request) {
        return ResponseEntity.ok(categoryService.addNewCategoryDTO(request));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(
            @NotNull
            @PositiveOrZero
            @PathVariable
            Long categoryId) {
        categoryService.deleteById(categoryId);
    }
}
