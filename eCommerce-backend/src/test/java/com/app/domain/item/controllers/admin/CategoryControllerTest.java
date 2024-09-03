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
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void deleteCategory_returnOk() throws Exception {
        final long id = 1;
        doNothing().when(categoryService).deleteById(id);

        mockMvc.perform(delete(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteCategory_returnNotFound() throws Exception {
        final long id = 1;
        doThrow(new CategoryNotFoundException()).when(categoryService).deleteById(id);

        mockMvc.perform(delete(CategoryController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CATEGORY_NOT_FOUND_MESSAGE)));
    }

    @Test
    void addNewCategory_returnOk() throws Exception {
        Category category = new RandomCategoryBuilder()
                .withId()
                .create();
        CategoryDTO categoryDTO = CategoryMapper.toCategoryDTO(category);
        NewCategoryRequest request = new NewCategoryRequest(null, category.getTitle());
        String requestJSON = StringUtils.toJSON(request);
        given(categoryService.addNewCategoryDTO(request)).willReturn(categoryDTO);


        mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(categoryDTO.id())))
                .andExpect(jsonPath("$.title", is(categoryDTO.title())));
    }

    @Test
    void addNewCategory_returnNotFound() throws Exception {
        String requestJSON = StringUtils.toJSON(new NewCategoryRequest(NumberUtils.getId(), RandomCategoryBuilder.getTitle()));
        doThrow(new ParentCategoryNotFoundException()).when(categoryService).addNewCategoryDTO(any());

        mockMvc.perform(post(CategoryController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.PARENT_CATEGORY_NOT_FOUND_MESSAGE)));
    }
}
