package com.app.domain.item.controllers.admin;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

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
        return ResponseEntity.ok(categoryService.addNewCategory(request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(
            @NotNull
            @PositiveOrZero
            @PathVariable
            Long categoryId) {
        categoryService.deleteById(categoryId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{categoryId}/{newTitle}")
    public ResponseEntity<CategoryDTO> modify(
            @PathVariable("categoryId")
            @NotNull
            @PositiveOrZero
            Long categoryId,
            @PathVariable("newTitle")
            @NotBlank
            @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
            String title) {
        return ResponseEntity.ok(categoryService.modify(categoryId, title));
    }

}
