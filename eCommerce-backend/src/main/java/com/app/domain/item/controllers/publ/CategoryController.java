package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.RestEndpoints;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO categoryDTO = categoryService.findDtoById(id);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByParentId(@PathVariable Long parentId) {
        List<CategoryDTO> children = categoryService.findDtosByParentId(parentId);
        return ResponseEntity.ok(children);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<CategoryDTO> getCategoriesByTitle(@PathVariable String title) {
        CategoryDTO category = categoryService.findDtoByTitle(title);
        return ResponseEntity.ok(category);
    }
}
