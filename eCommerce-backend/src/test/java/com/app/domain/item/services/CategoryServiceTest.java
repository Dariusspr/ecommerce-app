package com.app.domain.item.services;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.global.NumberUtils;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
@Tag("Integration test")
public class CategoryServiceTest {

    private static final int CATEGORY_ROOT_COUNT = 3;

    @Autowired
    private CategoryService categoryService;


    @Test
    void save_singleCategory() {
        Category category = new RandomCategoryBuilder().create();

        Category returnedCategory = categoryService.save(category);

        assertEquals(category, returnedCategory);
    }

    @Test
    void save_categoryWithParentAndWithNestedChildren() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .withNestedChildren()
                .create();

        Category returnedCategory = categoryService.save(category);

        assertEquals(category, returnedCategory);
    }

    @Test
    void save_singleCategory_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder().create();
        category.setTitle("1");

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void save_categoryWithParent_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .create();
        category.getParent().setTitle(null);

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void save_categoryWithChildren_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        category.getChildren().getFirst().setTitle("");

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void save_categoryWithChildren_NotUniqueTitle_throwsDataIntegrityViolationException() {
        Category category1 = new RandomCategoryBuilder()
                .withChildren()
                .create();
        Category category2 = new RandomCategoryBuilder()
                .withChildren()
                .create();
        category2.setTitle(category1.getTitle());

        categoryService.save(category1);

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.save(category2));
    }

    @Test
    void save_singleCategory_modifyTitle() {
        Category category = new RandomCategoryBuilder().create();
        Category returned = categoryService.save(category);
        assertEquals(category, returned);
        returned.setTitle(category.getTitle());

        Category returnedModified = categoryService.save(category);

        assertEquals(returned, returnedModified);
        assertEquals(returned.getId(), returnedModified.getId());
    }

    @Test
    void findById_singleCategory() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);

        Category returnedCategory = categoryService.findById(category.getId());

        assertEquals(category, returnedCategory);
    }

    @Test
    void findById__categoryWithParent_AccessingRelatedEntities_ThrowsLazyInitializationException() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .create();
        categoryService.save(category);

        Category returnedCategory = categoryService.findById(category.getId());

        assertEquals(category, returnedCategory);
        assertThrows(LazyInitializationException.class, () -> returnedCategory.getParent().getTitle());
    }

    @Test
    void findById_singleCategory_throwsCategoryNotFoundException() {
        Long id = NumberUtils.getId();

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void findByTitle_singleCategory() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);

        Category returnedCategory = categoryService.findByTitle(category.getTitle());

        assertEquals(category, returnedCategory);
    }

    @Test
    void findByTitle__categoryWithChildren_AccessingRelatedEntities_ThrowsLazyInitializationException() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);

        Category returnedCategory = categoryService.findByTitle(category.getTitle());

        assertEquals(category, returnedCategory);
        assertThrows(LazyInitializationException.class, () -> returnedCategory.getChildren().size());
    }

    @Test
    void findByTitle_singleCategory_throwsCategoryNotFoundException() {
        String title = RandomCategoryBuilder.getTitle();

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findByTitle(title));
    }

    @Test
    void findRoots_multipleCategoriesWithNestedChildren() {
        List<Category> categoryList = new RandomCategoryBuilder()
                .withNestedChildren()
                .create(CATEGORY_ROOT_COUNT);
        categoryList.forEach(categoryService::save);

        List<Category> returnedCategories = categoryService.findRoots();

        assertEquals(categoryList, returnedCategories);
    }

    @Test
    void findByParentId_categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        List<Category> children = category.getChildren();
        categoryService.save(category);

        List<Category> categories = categoryService.findByParentId(category.getId());

        assertEquals(children, categories);
    }

    @Test
    void deleteById_singleCategory() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);
        long id = category.getId();

        categoryService.deleteById(id);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void deleteById_categoryWithParentWithNestedChildren() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .withNestedChildren()
                .create();
        categoryService.save(category);
        long id = category.getId();
        long parentId = category.getParent().getId();
        long childId = category.getChildren().getFirst().getId();
        long nestedChildId = category.getChildren().getFirst().getChildren().getFirst().getId();

        categoryService.deleteById(id);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
        assertDoesNotThrow(() -> categoryService.findById(parentId));
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(childId));
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(nestedChildId));
    }

    @Test
    void addNewCategory_noParent() {
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(null, title);

        Category returnedCategory = categoryService.addNewCategory(request);

        assertEquals(title, returnedCategory.getTitle());
        assertNotNull(returnedCategory.getId());
    }

    @Test
    void addNewCategory_withParentInDatabase() {
        Category parent = new RandomCategoryBuilder().create();
        categoryService.save(parent);
        final Long parentId = parent.getId();
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(parentId, title);

        Category returnedCategory = categoryService.addNewCategory(request);

        Category returnedParent = returnedCategory.getParent();
        assertEquals(title, returnedCategory.getTitle());
        assertNotNull(returnedCategory.getId());
        assertEquals(parentId, returnedParent.getId());
    }

    // DTO methods

    @Test
    void saveDto_categoryWithParentWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .withChildren()
                .create();

        CategoryDTO returnedCategoryDto = categoryService.saveDto(category);

        CategoryDTO categoryDto = CategoryMapper.toCategoryDTO(category);
        assertEquals(categoryDto, returnedCategoryDto);
    }

    @Test
    void findDTOById__singleCategory() {
        Category category = new RandomCategoryBuilder()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findDtoById(category.getId());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findDTOById__categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findDtoById(category.getId());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findDTOByTitle__singleCategory() {
        Category category = new RandomCategoryBuilder()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findDtoByTitle(category.getTitle());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findDTOByTitle__categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findDtoByTitle(category.getTitle());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findRootsDto_multipleCategoriesWithNestedChildren() {
        List<Category> categoryList = new RandomCategoryBuilder()
                .withNestedChildren()
                .create(CATEGORY_ROOT_COUNT);
        categoryList.forEach(categoryService::save);
        List<CategoryDTO> categoryDTOList = categoryList.stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();

        List<CategoryDTO> returnedDto = categoryService.findRootsDto();

        assertEquals(categoryDTOList, returnedDto);
    }

    @Test
    void findDtosByParentId_categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);
        List<CategoryDTO> childrenDtos = category.getChildren()
                .stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();

        List<CategoryDTO> returnedDtos = categoryService.findDtosByParentId(category.getId());

        assertEquals(childrenDtos, returnedDtos);
    }

    @Test
    void addNewCategoryDTO_noParent() {
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(null, title);

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategoryDTO(request);

        assertEquals(title, returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }

    @Test
    void addNewCategoryDTO_withParentInDatabase() {
        Category parent = new RandomCategoryBuilder().create();
        categoryService.save(parent);
        final Long parentId = parent.getId();
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(parentId, title);

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategoryDTO(request);

        assertEquals(title, returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }
}
