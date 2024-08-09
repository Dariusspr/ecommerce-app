package com.app.domain.item.controllers.admin;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.RestEndpoints;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("adminCategoryController")
@RequestMapping(CategoryController.BASE_URL)
public class CategoryController {
    public static final String BASE_URL = RestEndpoints.ADMIN_API + "/categories";

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> addNewCategory(@RequestBody NewCategoryRequest request) {
        ResponseEntity<CategoryDTO> responseEntity = ResponseEntity.ok(categoryService.addNewCategoryDTO(request));
        return responseEntity;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable long categoryId) {
        categoryService.deleteById(categoryId);
    }
}
