package com.app.domain.item.controllers.members;

import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifyItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.services.ItemService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.item.RandomItemBuilder;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.app.domain.item.controllers.members.ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Item item;
    private NewItemRequest itemRequest;
    private ItemSummaryDTO itemSummaryDTO;
    private MockMultipartFile mockFile;
    private ModifyItemRequest modifyItemRequest;

    @BeforeAll
    void setup() {
        item = new RandomItemBuilder()
                .withId()
                .create();
        itemRequest = new NewItemRequest(item.getTitle(), item.getPrice(),
                item.getDescription(), Collections.emptyList(),
                null);
        itemSummaryDTO = ItemMapper.toItemSummaryDTO(item);
        mockFile = new MockMultipartFile(
                "media",
                "image.png",
                "image.png",
                "<Image>".getBytes()
        );
        modifyItemRequest = new ModifyItemRequest(item.getTitle(),
                null, null, null,
                null, List.of(mockFile), null);
    }

    @Test
    void delete_returnOk() throws Exception {
        doNothing().when(itemService).deleteById(item.getId());

        mockMvc.perform(delete(ItemController.BASE_URL + "/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returnForbidden() throws Exception {
        doThrow(new ForbiddenException()).when(itemService).deleteById(item.getId());

        mockMvc.perform(delete(ItemController.BASE_URL + "/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void create_returnOk() throws Exception {
        given(itemService.create(any(NewItemRequest.class))).willReturn(itemSummaryDTO);

        mockMvc.perform(MockMvcRequestBuilders.multipart(ItemController.BASE_URL)
                        .file(mockFile)
                        .param("title", itemRequest.title())
                        .param("price", itemRequest.price().toString())
                        .param("description", itemRequest.description())
                        .param("categoryId", itemRequest.categoryId() == null ? "" : itemRequest.categoryId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemSummaryDTO.id().toString())))
                .andExpect(jsonPath("$.title", is(itemRequest.title())))
                .andExpect(jsonPath("$.price", is(itemRequest.price().doubleValue())))
                .andExpect(jsonPath("$.media", is(Collections.emptyList())));
    }

    @Test
    void create_returnNotFound() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemService).create(any());

        mockMvc.perform(MockMvcRequestBuilders.multipart(ItemController.BASE_URL)
                        .file(mockFile)
                        .param("title", itemRequest.title())
                        .param("price", itemRequest.price().toString())
                        .param("description", itemRequest.description())
                        .param("categoryId", itemRequest.categoryId() == null ? "" : itemRequest.categoryId().toString())
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_returnOk() throws Exception {
        given(itemService.modify(item.getId(), modifyItemRequest)).willReturn(itemSummaryDTO);

        mockMvc.perform(MockMvcRequestBuilders.multipart(ItemController.BASE_URL + "/" + item.getId())
                        .file(mockFile)
                        .param("title", modifyItemRequest.title())
                        .param("price", modifyItemRequest.price() == null ? "" : modifyItemRequest.price().toString())
                        .param("description", modifyItemRequest.description())
                        .param("categoryId", modifyItemRequest.categoryId() == null ? "" : modifyItemRequest.categoryId().toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemSummaryDTO.id().toString())))
                .andExpect(jsonPath("$.title", is(modifyItemRequest.title())))
                .andExpect(jsonPath("$.price", is(itemSummaryDTO.price().doubleValue())))
                .andExpect(jsonPath("$.media", is(Collections.emptyList())));
    }

    @Test
    void modify_returnException() throws Exception {
        doThrow(new ForbiddenException()).when(itemService).modify(item.getId(), modifyItemRequest);

        mockMvc.perform(MockMvcRequestBuilders.multipart(ItemController.BASE_URL + "/" + item.getId())
                        .file(mockFile)
                        .param("title", modifyItemRequest.title())
                        .param("price", modifyItemRequest.price() == null ? "" : modifyItemRequest.price().toString())
                        .param("description", modifyItemRequest.description())
                        .param("categoryId", modifyItemRequest.categoryId() == null ? "" : modifyItemRequest.categoryId().toString())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }
}