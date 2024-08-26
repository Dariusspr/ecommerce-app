package com.app.domain.item.controllers.members;

import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.dtos.requests.ModifiedItemRequest;
import com.app.domain.item.dtos.requests.NewItemRequest;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.services.ItemService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.global.exceptions.ForbiddenException;
import com.app.utils.domain.item.RandomItemBuilder;
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

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.app.domain.item.controllers.members.ItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void delete_returnOk() throws Exception {
        final UUID id = UUID.randomUUID();
        doNothing().when(itemService).deleteById(any());

        mockMvc.perform(delete(ItemController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returnForbidden() throws Exception {
        final UUID id = UUID.randomUUID();
        doThrow(new ForbiddenException()).when(itemService).deleteById(id);

        mockMvc.perform(delete(ItemController.BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }

    @Test
    void create_returnOk() throws Exception {
        Item item = new RandomItemBuilder().create();
        NewItemRequest itemRequest = new NewItemRequest(item.getTitle(), item.getPrice(), item.getDescription(), Collections.emptyList(), null);
        ItemSummaryDTO itemSummaryDTO = ItemMapper.toItemSummaryDTO(item);
        String requestJSON = StringUtils.toJSON(itemRequest);
        given(itemService.create(itemRequest)).willReturn(itemSummaryDTO);

        mockMvc.perform(post(ItemController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemSummaryDTO.id())))
                .andExpect(jsonPath("$.title", is(itemRequest.title())))
                .andExpect(jsonPath("$.price", is(itemRequest.price().doubleValue())))
                .andExpect(jsonPath("$.media", is(Collections.emptyList())));
    }

    @Test
    void create_returnException() throws Exception {
        Item item = new RandomItemBuilder().create();
        NewItemRequest itemRequest = new NewItemRequest(item.getTitle(), item.getPrice(), item.getDescription(), Collections.emptyList(), null);
        String requestJSON = StringUtils.toJSON(itemRequest);
        doThrow(new ItemNotFoundException()).when(itemService).create(any());

        mockMvc.perform(post(ItemController.BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void modify_returnOk() throws Exception {
        Item item = new RandomItemBuilder().withId().create();
        ModifiedItemRequest itemRequest = new ModifiedItemRequest(item.getTitle(), null, null, null, null);
        ItemSummaryDTO itemSummaryDTO = ItemMapper.toItemSummaryDTO(item);
        String requestJSON = StringUtils.toJSON(itemRequest);
        given(itemService.modify(item.getId(), itemRequest)).willReturn(itemSummaryDTO);


        mockMvc.perform(put(ItemController.BASE_URL + "/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemSummaryDTO.id().toString())))
                .andExpect(jsonPath("$.title", is(itemRequest.title())))
                .andExpect(jsonPath("$.price", is(item.getPrice().doubleValue())))
                .andExpect(jsonPath("$.media", is(Collections.emptyList())));
    }

    @Test
    void modify_returnException() throws Exception {
        Item item = new RandomItemBuilder().withId().create();
        ModifiedItemRequest itemRequest = new ModifiedItemRequest(item.getTitle(), null, null, null, null);
        String requestJSON = StringUtils.toJSON(itemRequest);
        doThrow(new ForbiddenException()).when(itemService).modify(item.getId(), itemRequest);

        mockMvc.perform(put(ItemController.BASE_URL + "/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJSON))
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.FORBIDDEN_MESSAGE)));
    }
}