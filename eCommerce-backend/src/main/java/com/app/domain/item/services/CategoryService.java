package com.app.domain.item.services;


import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.domain.item.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Category category) {
        return save(category);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public Category findByTitle(String title) {
        return categoryRepository.findByTitle(title)
                .orElseThrow(CategoryNotFoundException::new);
    }

    public List<Category> findRoots() {
        return categoryRepository.findRoots();
    }

    public List<Category> findByParentId(Long id) {
        return categoryRepository.findByParentId(id);
    }
    @Transactional
    public CategoryDTO findDtoByTitle(String title) {
        Category category = findByTitle(title);
        return CategoryMapper.toCategoryDTO(category);
    }

    @Transactional
    public CategoryDTO findDtoById(Long id) {
        Category category = findById(id);
        return CategoryMapper.toCategoryDTO(category);
    }

    @Transactional
    public List<CategoryDTO> findRootsDto() {
        List<Category> categories = findRoots();
        return categories.stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();
    }

    @Transactional
    public List<CategoryDTO> findDtosByParentId(Long id) {
        List<Category> categories = findByParentId(id);
        return categories.stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();
    }
}
