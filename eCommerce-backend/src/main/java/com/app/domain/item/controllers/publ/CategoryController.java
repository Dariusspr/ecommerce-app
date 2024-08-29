package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.RestEndpoints;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

@RestController("publicCategoryController")
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {
    public static final String BASE_URL = RestEndpoints.PUBLIC_API + "/categories";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        List<CategoryDTO> roots = categoryService.findRootsDto();
        return ResponseEntity.ok(roots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @PathVariable
            @NotNull
            @PositiveOrZero
            Long id) {
        CategoryDTO categoryDTO = categoryService.findDtoById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByParentId(
            @PathVariable
            @NotNull
            @PositiveOrZero
            Long parentId) {
        List<CategoryDTO> children = categoryService.findDtosByParentId(parentId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<CategoryDTO> getCategoriesByTitle(
            @PathVariable
            @NotBlank
            @Size(min = TITLE_LENGTH_MIN, max = TITLE_LENGTH_MAX)
            String title) {
        CategoryDTO category = categoryService.findDtoByTitle(title);
        return ResponseEntity.ok(category);
    }
}
