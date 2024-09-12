package com.app.domain.item.services;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.DuplicateCategoryException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.domain.item.repositories.CategoryRepository;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.global.NumberUtils;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CategoryServiceTest {

    private static final int CATEGORY_ROOT_COUNT = 3;
    private static final int CATEGORY_COUNT_MIN = 3;
    private static final int CATEGORY_COUNT_MAX = 15;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;

    private Category categorySingle;
    private Category categoryWithChildrenAndParent;

    @BeforeEach
    void setup() {
        categorySingle = new RandomCategoryBuilder()
                .create();

        categoryWithChildrenAndParent = new RandomCategoryBuilder()
                .withParent()
                .withNestedChildren()
                .create();
    }

    @AfterEach
    void clear() {
        categoryRepository.deleteAll();
    }

    @Test
    void addNewCategory_noParent_ok() {
        NewCategoryRequest categorySingleRequest = new NewCategoryRequest(null, categorySingle.getTitle());

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategory(categorySingleRequest);

        assertEquals(categorySingle.getTitle(), returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }

    @Test
    void addNewCategory_withParent_ok() {
        categoryService.save(categorySingle);
        final Long parentId = categorySingle.getId();
        final String title = RandomCategoryBuilder.getTitle();
        NewCategoryRequest request = new NewCategoryRequest(parentId, title);

        CategoryDTO returnedCategoryDTO = categoryService.addNewCategory(request);

        assertEquals(title, returnedCategoryDTO.title());
        assertNotNull(returnedCategoryDTO.id());
    }

    @Test
    void addNewCategory_throwParentCategoryNotFound() {
        NewCategoryRequest request = new NewCategoryRequest(NumberUtils.getId() + 1,
                categorySingle.getTitle());

        assertThrows(ParentCategoryNotFoundException.class,
                () -> categoryService.addNewCategory(request));
    }

    @Test
    void addNew_throwDuplicateCategory() {
        categoryService.save(categorySingle);
        NewCategoryRequest request = new NewCategoryRequest(null, categorySingle.getTitle());

        assertThrows(DuplicateCategoryException.class,
                () -> categoryService.addNewCategory(request));
    }

    @Test
    void save_singleCategory() {
        Category returnedCategory = categoryService.save(categorySingle);

        assertEquals(categorySingle, returnedCategory);
    }

    @Test
    void save_categoryWithParentAndWithNestedChildren() {

        Category returnedCategory = categoryService.save(categoryWithChildrenAndParent);

        assertEquals(categoryWithChildrenAndParent, returnedCategory);
    }

    @Test
    void save_singleCategory_invalidTitle_throwsConstraintViolationException() {
        categorySingle.setTitle("1");

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(categorySingle));
    }


    @Test
    void save_singleCategory_modifyTitle() {
        Category returned = categoryService.save(categorySingle);
        returned.setTitle(RandomCategoryBuilder.getTitle());

        Category returnedModified = categoryService.save(categorySingle);

        assertEquals(returned, returnedModified);
        assertEquals(returned.getId(), returnedModified.getId());
    }

    @Test
    void save_duplicateTitle_throwDuplicateCategory() {
        categoryService.save(categorySingle);
        Category category = new RandomCategoryBuilder().create();
        category.setTitle(categorySingle.getTitle());

        assertThrows(DuplicateCategoryException.class, () -> categoryService.save(category));
    }

    @Test
    void modify_ok() {
        categoryService.save(categorySingle);
        String newTitle = categorySingle.getTitle() + "A";

        CategoryDTO returnedCategory = categoryService.modify(categorySingle.getId(), newTitle);
        assertEquals(categorySingle.getId(), returnedCategory.id());
        assertNotEquals(categorySingle.getTitle(), returnedCategory.title());
    }

    @Test
    void modify_throwCategoryNotFound() {
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.modify(NumberUtils.getId(), categorySingle.getTitle()));
    }


    @Test
    void findById_singleCategory() {
        categoryService.save(categorySingle);

        Category returnedCategory = categoryService.findById(categorySingle.getId());

        assertEquals(categorySingle, returnedCategory);
    }

    @Test
    void findById_singleCategory_throwCategoryNotFoundException() {
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.findById(NumberUtils.getId()));
    }

    @Test
    void findByTitle__singleCategory() {
        categoryService.save(categorySingle);
        CategoryDTO categorySingleDTO = CategoryMapper.toCategoryDTO(categorySingle);

        CategoryDTO returnedDto = categoryService.findByTitle(categorySingle.getTitle());

        assertEquals(categorySingleDTO, returnedDto);
    }

    @Test
    void findByTitle__categoryWithChildren() {
        categoryService.save(categoryWithChildrenAndParent);
        CategoryDTO categoryWithChildrenAndParentDTO = CategoryMapper.toCategoryDTO(categoryWithChildrenAndParent);

        CategoryDTO returnedDto = categoryService.findByTitle(categoryWithChildrenAndParent.getTitle());

        assertEquals(categoryWithChildrenAndParentDTO, returnedDto);
    }

    @Test
    void findByTitle_singleCategory_throwsCategoryNotFound() {
        assertThrows(CategoryNotFoundException.class,
                () -> categoryService.findByTitle(categorySingle.getTitle()));
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
        categoryService.save(categorySingle);
        Set<Category> returnedCategories = categoryService.findFrom(categorySingle.getId());

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
    void findByParentId_categoryWithChildrenAndParent() {
        categoryService.save(categoryWithChildrenAndParent);
        List<CategoryDTO> childrenDtos = categoryWithChildrenAndParent.getChildren()
                .stream()
                .map(CategoryMapper::toCategoryDTO)
                .toList();

        List<CategoryDTO> returnedDtos = categoryService.findByParentId(categoryWithChildrenAndParent.getId());

        assertEquals(childrenDtos, returnedDtos);
    }

    @Test
    void findByParentId_throwParentCategoryNotFound() {
        assertThrows(ParentCategoryNotFoundException.class,
                () -> categoryService.findByParentId(NumberUtils.getId()));
    }

    @Test
    void findDTOById__singleCategory() {
        categoryService.save(categorySingle);
        CategoryDTO categorySingleDTO = CategoryMapper.toCategoryDTO(categorySingle);

        CategoryDTO returnedDto = categoryService.findDtoById(categorySingle.getId());

        assertEquals(categorySingleDTO, returnedDto);
    }

    @Test
    void findDTOById__categoryWithChildrenAndParent() {
        categoryService.save(categoryWithChildrenAndParent);
        CategoryDTO categoryWithChildrenAndParentDTO = CategoryMapper.toCategoryDTO(categoryWithChildrenAndParent);

        CategoryDTO returnedDto = categoryService.findDtoById(categoryWithChildrenAndParent.getId());

        assertEquals(categoryWithChildrenAndParentDTO, returnedDto);
    }

    @Test
    void deleteById_singleCategory() {
        categoryService.save(categorySingle);
        final long id = categorySingle.getId();

        categoryService.deleteById(id);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
    }

    @Test
    void deleteById_categoryWithChildrenAndParent() {
        categoryService.save(categoryWithChildrenAndParent);
        final long id = categoryWithChildrenAndParent.getId();
        final long parentId = categoryWithChildrenAndParent.getParent().getId();
        final long childId = categoryWithChildrenAndParent.getChildren()
                .getFirst()
                .getId();
        final long nestedChildId = categoryWithChildrenAndParent.getChildren()
                .getFirst()
                .getChildren()
                .getFirst()
                .getId();

        categoryService.deleteById(id);

        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(id));
        assertDoesNotThrow(() -> categoryService.findById(parentId));
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(childId));
        assertThrows(CategoryNotFoundException.class, () -> categoryService.findById(nestedChildId));
    }
}
