package com.app.domain.item.controllers.admin;

import com.app.domain.item.dtos.CategoryDTO;
import com.app.domain.item.dtos.requests.NewCategoryRequest;
import com.app.domain.item.entities.Category;
import com.app.domain.item.exceptions.CategoryNotFoundException;
import com.app.domain.item.exceptions.ParentCategoryNotFoundException;
import com.app.domain.item.mappers.CategoryMapper;
import com.app.domain.item.services.CategoryService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.item.RandomCategoryBuilder;
import com.app.utils.global.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Category category;
    private CategoryDTO categoryDTO;
    private NewCategoryRequest newCategoryRequest;
    private String newCategoryRequestJSON;

    @BeforeAll
    void setup() throws JsonProcessingException {
        category = new RandomCategoryBuilder()
                .withId()
                .create();
        categoryDTO = CategoryMapper.toCategoryDTO(category);
        newCategoryRequest = new NewCategoryRequest(null, category.getTitle());
        newCategoryRequestJSON = StringUtils.toJSON(newCategoryRequest);
    }

    @Test
    void addNewCategory_returnOk() throws Exception {
        given(categoryService.addNewCategory(newCategoryRequest)).willReturn(categoryDTO);


        mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryRequestJSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDTO.id().intValue())))
                .andExpect(jsonPath("$.title", is(categoryDTO.title())));
    }

    @Test
    void addNewCategory_returnNotFound() throws Exception {
        doThrow(new ParentCategoryNotFoundException()).when(categoryService).addNewCategory(any());

        mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newCategoryRequestJSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.PARENT_CATEGORY_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_returnOK() throws Exception {
        given(categoryService.modify(categoryDTO.id(), categoryDTO.title())).willReturn(categoryDTO);

        mockMvc.perform(put(CategoryController.BASE_URL + "/" + categoryDTO.id() + "/" + categoryDTO.title())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDTO.id().intValue())))
                .andExpect(jsonPath("$.title", is(categoryDTO.title())));
    }

    @Test
    void modify_returnNotFound() throws Exception {
        doThrow(new CategoryNotFoundException()).when(categoryService).modify(category.getId(), category.getTitle());

        mockMvc.perform(put(CategoryController.BASE_URL + "/" + category.getId() + "/" + category.getTitle())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE)));
    }

    @Test
    void deleteCategory_returnOk() throws Exception {
        doNothing().when(categoryService).deleteById(category.getId());

        mockMvc.perform(delete(CategoryController.BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_returnNotFound() throws Exception {
        doThrow(new CategoryNotFoundException()).when(categoryService).deleteById(category.getId());

        mockMvc.perform(delete(CategoryController.BASE_URL + "/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE)));
    }


}
