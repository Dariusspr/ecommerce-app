package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.domain.item.services.CategoryService;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.item.RandomCategoryBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("Unit test")
@WebMvcTest(controllers = CategoryController.class)
@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void getRootCategories_returnsOkEmpty() throws Exception {
        given(categoryService.findRootsDto()).willReturn(Collections.emptyList());

        mockMvc.perform(get(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void getRootCategories_returnOkFive() throws Exception {
        final int rootCount = 5;
        List<Category> categoryList = new RandomCategoryBuilder()
                .withNestedChildren()
                .withId()
                .create(rootCount);
        List<CategoryDTO> categoryDTOList = CategoryMapper.toCategoryDTO(categoryList);
        given(categoryService.findRootsDto()).willReturn(categoryDTOList);

        mockMvc.perform(get(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(rootCount)))
                .andExpect(jsonPath("$[0].id", is(categoryDTOList.getFirst().id())))
                .andExpect(jsonPath("$[0].title", is(categoryDTOList.getFirst().title())))
                .andExpect(jsonPath("$[0].hasChildren", is(categoryDTOList.getFirst().hasChildren())))
                .andExpect(jsonPath("$[1].id", is(categoryDTOList.get(1).id())));
    }

    @Test
    public void getCategoryById_returnBadRequest() throws Exception {
        long id = 1;
        given(categoryService.findDtoById(id)).willThrow(new CategoryNotFoundException());

        mockMvc.perform(get(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE)));
    }

    @Test
    public void getCategoryById_returnOk() throws Exception {
        Category category = new RandomCategoryBuilder().withId().create();
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);
        given(categoryService.findDtoById(category.getId())).willReturn(categoryDTO);

        mockMvc.perform(get(CategoryController.BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(categoryDTO.id())))
                .andExpect(jsonPath("$.title", is(categoryDTO.title())))
                .andExpect(jsonPath("$.hasChildren", is(categoryDTO.hasChildren())));
    }

    @Test
    public void getCategoryByTitle_returnBadRequest() throws Exception {
        String title = RandomCategoryBuilder.getTitle();
        given(categoryService.findDtoByTitle(title)).willThrow(new CategoryNotFoundException());

        mockMvc.perform(get(CategoryController.BASE_URL + "/title/" + title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE)));
    }

    @Test
    public void getCategoryByTitle_returnOk() throws Exception {
        Category category = new RandomCategoryBuilder().withId().create();
        String title = category.getTitle();
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);
        given(categoryService.findDtoByTitle(title)).willReturn(categoryDTO);

        mockMvc.perform(get(CategoryController.BASE_URL + "/title/" + title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(categoryDTO.id())))
                .andExpect(jsonPath("$.title", is(categoryDTO.title())))
                .andExpect(jsonPath("$.hasChildren", is(categoryDTO.hasChildren())));
    }

    @Test
    public void getCategoriesByParentId_returnOkEmpty() throws Exception {
        long parentId = 1;
        given(categoryService.findByParentId(parentId)).willReturn(Collections.emptyList());

        mockMvc.perform(get(CategoryController.BASE_URL + "/parent/" + parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void getCategoriesByParentId_returnOkFive() throws Exception {
        long parentId = 1;
        final int childrenCount = 5;
        List<Category> categoryList = new RandomCategoryBuilder().withId().create(childrenCount);
        List<CategoryDTO> categoryDTOList = CategoryMapper.toCategoryDTO(categoryList);
        given(categoryService.findDtosByParentId(parentId)).willReturn(categoryDTOList);

        mockMvc.perform(get(CategoryController.BASE_URL + "/parent/" + parentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(childrenCount)))
                .andExpect(jsonPath("$[0].id", is(categoryDTOList.getFirst().id())))
                .andExpect(jsonPath("$[0].title", is(categoryDTOList.getFirst().title())))
                .andExpect(jsonPath("$[0].hasChildren", is(categoryDTOList.getFirst().hasChildren())))
                .andExpect(jsonPath("$[1].id", is(categoryDTOList.get(1).id())));
    }

}
