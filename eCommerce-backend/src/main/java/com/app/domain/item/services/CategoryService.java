package com.app.domain.item.services;


import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.DuplicateCategoryException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.domain.item.repositories.CategoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDTO addNewCategory(NewCategoryRequest request) {
        Category category = CategoryMapper.toCategory(request.title());
        save(category);
        if (request.parentId() != null) {
            try {
                Category parent = findById(request.parentId());
                parent.addChild(category);
                save(parent);
            } catch (CategoryNotFoundException e) {
                throw new ParentCategoryNotFoundException();
            }
        }
        return CategoryMapper.toCategoryDTO(category);
    }

    @Transactional
    public CategoryDTO modify(Long categoryId, String newTitle) {
        Category category = findById(categoryId);
        category.setTitle(newTitle);
        save(category);
        return CategoryMapper.toCategoryDTO(category);
    }

    @Transactional
    public Category save(Category category) {
        try {
            return categoryRepository.saveAndFlush(category);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateCategoryException();
        }
    }

    @Transactional
    public void deleteById(Long id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public CategoryDTO findDtoById(Long id) {
        Category category = findById(id);
        return CategoryMapper.toCategoryDTO(category);
    }

    public CategoryDTO findByTitle(String title) {
        Category category = categoryRepository.findByTitle(title)
                .orElseThrow(CategoryNotFoundException::new);
        return CategoryMapper.toCategoryDTO(category);
    }

    public List<CategoryDTO> findRoots() {
        List<Category> categories = categoryRepository.findRoots();
        return categories.stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();
    }

    public List<CategoryDTO> findByParentId(Long id) {
        try {
            Category parent = findById(id);
            List<Category> categories = categoryRepository.findByParent(parent);

            return categories.stream()
                    .map(CategoryMapper::toCategoryDTO)
                    .toList();
        } catch (CategoryNotFoundException e) {
            throw new ParentCategoryNotFoundException();
        }
    }

    public Set<Category> findFrom(Long id) {
        Category category = findById(id);
        return findFrom(category);
    }

    protected Set<Category> findFrom(Category category) {
        Set<Category> categories = new HashSet<>();
        categories.add(category);
        for (Category child : category.getChildren()) {
            categories.addAll(findFrom(child));
        }
        return categories;
    }
}
