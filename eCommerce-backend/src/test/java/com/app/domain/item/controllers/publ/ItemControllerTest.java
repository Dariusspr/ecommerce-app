package com.app.domain.item.controllers.publ;

import com.app.domain.item.dtos.ItemDetailedDTO;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.entities.Item;
import com.app.domain.item.exceptions.ItemNotFoundException;
import com.app.domain.item.mappers.ItemMapper;
import com.app.domain.item.services.ItemService;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.item.RandomItemBuilder;
import com.app.utils.global.NumberUtils;
import com.app.utils.global.StringUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("Unit test")
@WebMvcTest(controllers = ItemController.class)
@ExtendWith(MockitoExtension.class)
public class ItemControllerTest {
    private static final int PAGE_NUMBER_0 = 0;
    private static final int PAGE_SIZE = 20;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private final int itemCount = 5;
    private final Pageable pageable = PageRequest.of(PAGE_NUMBER_0, PAGE_SIZE);

    @Test
    void getAll_returnOK() throws Exception {
        final List<Item> items = new RandomItemBuilder()
                .withCategory()
                .withId()
                .withMedia()
                .withAuditable()
                .create(itemCount);
        final List<ItemSummaryDTO> itemSummaryDTOList = items.stream().map(ItemMapper::toItemSummaryDTO).toList();
        final Page<ItemSummaryDTO> itemSummaryDTOPage = new PageImpl<>(
                itemSummaryDTOList,
                pageable,
                itemSummaryDTOList.size());
        given(itemService.findAll(pageable)).willReturn(itemSummaryDTOPage);

        mockMvc.perform(get(ItemController.BASE_URL)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(itemCount)));
    }

    @Test
    void getAll_returnEmpty() throws Exception {
        given(itemService.findAll(pageable)).willReturn(Page.empty());

        mockMvc.perform(get(ItemController.BASE_URL)
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(0)));
    }

    @Test
    void getById_returnOk() throws Exception {
        final Item item = new RandomItemBuilder()
                .withCategory()
                .withId()
                .withMedia()
                .withAuditable()
                .create();
        final ItemDetailedDTO itemDetailedDTO = ItemMapper.toItemDetailedDTO(item);
        given(itemService.findDetailedById(item.getId())).willReturn(itemDetailedDTO);

        mockMvc.perform(get(ItemController.BASE_URL + "/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(item.getId().toString())))
                .andExpect(jsonPath("$.title", is(item.getTitle())))
                .andExpect(jsonPath("$.price", is(item.getPrice().doubleValue())))
                .andExpect(jsonPath("$.seller.id", is(item.getSeller().getId())))
                .andExpect(jsonPath("$.seller.username", is(item.getSeller().getUsername())))
                .andExpect(jsonPath("$.seller.profile.title", is(item.getSeller().getProfile().title())))
                .andExpect(jsonPath("$.seller.profile.url", is(item.getSeller().getProfile().url())))
                .andExpect(jsonPath("$.seller.profile.format", is(item.getSeller().getProfile().format().toString())))
                .andExpect(jsonPath("$.media.size()", is(item.getMediaList().size())))
                .andExpect(jsonPath("$.category", is(item.getCategory().getTitle())))
                .andExpect(jsonPath("$.createdDate", is(item.getCreatedDate().truncatedTo(ChronoUnit.SECONDS).toString())))
                .andExpect(jsonPath("$.lastModifiedData", is(item.getLastModifiedDate().truncatedTo(ChronoUnit.SECONDS).toString())));
    }

    @Test
    void getById_returnBadRequest() throws Exception {
        doThrow(new ItemNotFoundException()).when(itemService).findDetailedById(any());

        mockMvc.perform(get(ItemController.BASE_URL + "/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(ExceptionMessages.ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void getByTitle_returnOk() throws Exception {
        final List<Item> items = new RandomItemBuilder()
                .withCategory()
                .withId()
                .withMedia()
                .withAuditable()
                .create(itemCount);
        final List<ItemSummaryDTO> itemSummaryDTOList = items.stream().map(ItemMapper::toItemSummaryDTO).toList();
        final Page<ItemSummaryDTO> itemSummaryDTOPage = new PageImpl<>(
                itemSummaryDTOList,
                pageable,
                itemSummaryDTOList.size());
        given(itemService.findByTitle(anyString(), any())).willReturn(itemSummaryDTOPage);

        mockMvc.perform(get(ItemController.BASE_URL + "/title/" + StringUtils.getText(1))
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(itemCount)));
    }

    @Test
    void getByTitle_returnEmpty() throws Exception {
        given(itemService.findByTitle(anyString(), any())).willReturn(Page.empty());

        mockMvc.perform(get(ItemController.BASE_URL + "/title/" + StringUtils.getText(1))
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(0)));
    }

    @Test
    void getBySeller_returnOk() throws Exception {
        final List<Item> items = new RandomItemBuilder()
                .withCategory()
                .withId()
                .withMedia()
                .withAuditable()
                .create(itemCount);
        final List<ItemSummaryDTO> itemSummaryDTOList = items.stream().map(ItemMapper::toItemSummaryDTO).toList();
        final Page<ItemSummaryDTO> itemSummaryDTOPage = new PageImpl<>(
                itemSummaryDTOList,
                pageable,
                itemSummaryDTOList.size());
        given(itemService.findBySellerId(anyLong(), any())).willReturn(itemSummaryDTOPage);

        mockMvc.perform(get(ItemController.BASE_URL + "/seller/" + NumberUtils.getId())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(itemCount)));
    }

    @Test
    void getBySeller_returnEmpty() throws Exception {
        given(itemService.findBySellerId(anyLong(), any())).willReturn(Page.empty());

        mockMvc.perform(get(ItemController.BASE_URL + "/seller/" + NumberUtils.getId())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(0)));
    }

    @Test
    void getByCategory_returnOk() throws Exception {
        final List<Item> items = new RandomItemBuilder()
                .withCategory()
                .withId()
                .withMedia()
                .withAuditable()
                .create(itemCount);
        final List<ItemSummaryDTO> itemSummaryDTOList = items.stream().map(ItemMapper::toItemSummaryDTO).toList();
        final Page<ItemSummaryDTO> itemSummaryDTOPage = new PageImpl<>(
                itemSummaryDTOList,
                pageable,
                itemSummaryDTOList.size());
        given(itemService.findByCategoryId(anyLong(), any())).willReturn(itemSummaryDTOPage);

        mockMvc.perform(get(ItemController.BASE_URL + "/category/" + NumberUtils.getId())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(itemCount)));
    }

    @Test
    void getByCategory_returnEmpty() throws Exception {
        given(itemService.findByCategoryId(anyLong(), any())).willReturn(Page.empty());

        mockMvc.perform(get(ItemController.BASE_URL + "/category/" + NumberUtils.getId())
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content.size()", is(0)));
    }

}
