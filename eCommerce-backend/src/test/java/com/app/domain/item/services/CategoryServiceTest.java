package com.app.domain.item.services;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.DuplicateCategoryException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.global.NumberUtils;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CategoryServiceTest {

    private static final int CATEGORY_ROOT_COUNT = 3;
    private static final int CATEGORY_COUNT_MIN = 3;
    private static final int CATEGORY_COUNT_MAX = 15;

    @Autowired
    private CategoryService categoryService;

    @Test
    void addNewCategory_noParent_ok() {
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(null, title);

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategory(request);

        assertEquals(title, returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }

    @Test
    void addNewCategory_withParent_ok() {
        Category parent = new RandomCategoryBuilder().create();
        categoryService.save(parent);
        final Long parentId = parent.getId();
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(parentId, title);

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategory(request);

        assertEquals(title, returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }

    @Test
    void addNewCategory_noParent_throwParentCategoryNotFound() {
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(NumberUtils.getId() + 1, title);

        assertThrows(ParentCategoryNotFoundException.class, () -> categoryService.addNewCategory(request));
    }

    @Test
    void addNew_throwDuplicateCategory() {
        Category categoryInDB1 = new RandomCategoryBuilder().create();
        categoryService.save(categoryInDB1);
        NewCategoryRequest request = new NewCategoryRequest(null, categoryInDB1.getTitle());

        assertThrows(DuplicateCategoryException.class, () -> categoryService.addNewCategory(request));
    }

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
    void save_duplicateTitle_throwDuplicateCategory() {
        Category categoryInDB1 = new RandomCategoryBuilder().create();
        categoryService.save(categoryInDB1);
        Category category = new RandomCategoryBuilder().create();
        category.setTitle(categoryInDB1.getTitle());

        assertThrows(DuplicateCategoryException.class, () -> categoryService.save(category));
    }

    @Test
    void modify_ok() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);
        String newTitle = category.getTitle() + "A";

        CategoryDTO returnedCategory = categoryService.modify(category.getId(), newTitle);
        assertEquals(category.getId(), returnedCategory.id());
        assertNotEquals(category.getTitle(), returnedCategory.title());
    }

    @Test
    void modify_throwDuplicateCategory() {
        Category categoryInDB1 = new RandomCategoryBuilder().create();
        categoryService.save(categoryInDB1);
        Category categoryInDB2 = new RandomCategoryBuilder().create();
        categoryService.save(categoryInDB2);

        assertThrows(DuplicateCategoryException.class, () -> categoryService.modify(categoryInDB2.getId(), categoryInDB1.getTitle()));
    }

    @Test
    void modify_throwCategoryNotFound() {
        assertThrows(CategoryNotFoundException.class, () -> categoryService.modify(NumberUtils.getId(), RandomCategoryBuilder.getTitle()));
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
    void findByTitle__singleCategory() {
        Category category = new RandomCategoryBuilder()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findByTitle(category.getTitle());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findByTitle__categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);

        CategoryDTO returnedDto = categoryService.findByTitle(category.getTitle());

        assertEquals(categoryDTO, returnedDto);
    }

    @Test
    void findByTitle_singleCategory_throwsCategoryNotFound() {
        String title = RandomCategoryBuilder.getTitle();

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findByTitle(title));
    }

    @Test
    void findRoots_multipleCategoriesWithNestedChildren() {
        List<Category> categoryList = new RandomCategoryBuilder()
                .withNestedChildren()
                .create(CATEGORY_ROOT_COUNT);
        categoryList.forEach(categoryService::save);
        List<CategoryDTO> categoryDTOList = categoryList.stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();

        List<CategoryDTO> returnedDto = categoryService.findRoots();

        assertEquals(categoryDTOList, returnedDto);
    }

    @Test
    void findFrom_single() {
        Category category = new RandomCategoryBuilder().create();
        categoryService.save(category);
        Set<Category> returnedCategories = categoryService.findFrom(category.getId());

        assertEquals(1, returnedCategories.size());
    }

    @Test
    void findFrom_oneChildEach() {
        final int categoryCount = NumberUtils.getIntegerInRange(CATEGORY_COUNT_MIN, CATEGORY_COUNT_MAX);
        List<Category> categories = new RandomCategoryBuilder().create(categoryCount);
        Category category = RandomCategoryBuilder.chain(categories);
        categoryService.save(category);

        Set<Category> returnedCategories = categoryService.findFrom(category.getId());

        assertEquals(categoryCount, returnedCategories.size());
    }

    @Test
    void findFrom_threeChildEach() {
        final int categoryCount = NumberUtils.getIntegerInRange(CATEGORY_COUNT_MIN, CATEGORY_COUNT_MAX);
        final int childrenCount = NumberUtils.getIntegerInRange(CATEGORY_COUNT_MIN, CATEGORY_COUNT_MAX);
        List<Category> categories = new RandomCategoryBuilder().withChildren(childrenCount).create(categoryCount);
        Category category = RandomCategoryBuilder.chain(categories);
        categoryService.save(category);

        Set<Category> returnedCategories = categoryService.findFrom(category.getId());

        assertEquals(categoryCount * (childrenCount + 1), returnedCategories.size());
    }

    @Test
    void findByParentId_categoryWithChildren() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        categoryService.save(category);
        List<CategoryDTO> childrenDtos = category.getChildren()
                .stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();

        List<CategoryDTO> returnedDtos = categoryService.findByParentId(category.getId());

        assertEquals(childrenDtos, returnedDtos);
    }

    @Test
    void findByParentId_throwParentCategoryNotFound() {
        assertThrows(ParentCategoryNotFoundException.class, () -> categoryService.findByParentId(NumberUtils.getId()));
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
}
