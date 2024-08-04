package com.app.domain.item.services;

import com.app.domain.item.entities.Category;
import com.app.utils.domain.item.RandomCategoryBuilder;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("Integration test")
public class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;


    @Test
    void saveTest_singleCategory() {
        Category category = new RandomCategoryBuilder().create();

        Category returned = categoryService.save(category);

        assertEquals(category, returned);
    }

    @Test
    void saveTest_categoryWithParentAndWithNestedChildren() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .withNestedChildren()
                .create();

        Category returned = categoryService.save(category);

        assertEquals(category, returned);
    }

    @Test
    void saveTest_singleCategory_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder().create();
        category.setTitle("1");

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void saveTest_categoryWithParent_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder()
                .withParent()
                .create();
        category.getParent().setTitle(null);

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void saveTest_categoryWithChildren_invalidTitle_throwsConstraintViolationException() {
        Category category = new RandomCategoryBuilder()
                .withChildren()
                .create();
        category.getChildren().getFirst().setTitle("");

        assertThrows(ConstraintViolationException.class, () -> categoryService.save(category));
    }

    @Test
    void saveTest_categoryWithChildren_NotUniqueTitle_throwsDataIntegrityViolationException() {
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
    void updateTest_singleCategory_modifyTitle() {
        Category category = new RandomCategoryBuilder().create();

        Category returned = categoryService.save(category);
        assertEquals(category, returned);

        returned.setTitle(category.getTitle());
        Category returnedModified = categoryService.save(category);

        assertEquals(returned, returnedModified);
        assertEquals(returned.getId(), returnedModified.getId());
    }

}
