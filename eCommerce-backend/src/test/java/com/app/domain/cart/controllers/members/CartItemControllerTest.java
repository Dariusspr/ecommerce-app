package com.app.domain.cart.controllers.members;


import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.CartItemDTO;
import com.app.domain.cart.dtos.requests.CartItemRequest;
import com.app.domain.cart.exceptions.CartItemNotFoundException;
import com.app.domain.cart.exceptions.InsufficientStockException;
import com.app.domain.cart.services.CartItemService;
import com.app.domain.item.entities.Item;
import com.app.domain.item.mappers.ItemMapper;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
import com.app.utils.domain.item.RandomItemBuilder;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CartItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CartItemRequest itemRequest;
    private CartDTO cartDTO;
    private String itemRequestJson;

    @BeforeAll
    void setup() throws JsonProcessingException {
        Item item = new RandomItemBuilder()
                .withId()
                .create();
        itemRequest = new CartItemRequest(item.getId(), 1);
        CartItemDTO cartItemDTO = new CartItemDTO(1L,
                ItemMapper.toItemSummaryDTO(item),
                item.getQuantity(),
                item.getPrice());
        cartDTO = new CartDTO(UUID.randomUUID(), List.of(cartItemDTO), cartItemDTO.pricePerUnit());
        itemRequestJson = StringUtils.toJSON(itemRequest);
    }

    @Test
    void addItem_ok() throws Exception {
        given(cartItemService.addItemToCart(itemRequest)).willReturn(cartDTO);

        mockMvc.perform(post(CartItemController.BASE_URL)
                        .content(itemRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().doubleValue())))
                .andExpect(jsonPath("$.items.size()", is(1)));
    }

    @Test
    void addItem_badRequest() throws Exception {
        doThrow(new InsufficientStockException()).when(cartItemService).addItemToCart(itemRequest);

        mockMvc.perform(post(CartItemController.BASE_URL)
                        .content(itemRequestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.INSUFFICIENT_STOCK_MESSAGE)));
    }

    @Test
    void modifyCartItem_ok() throws Exception {
        given(cartItemService.modifyCartItem(1L, itemRequest.quantity())).willReturn(cartDTO);

        mockMvc.perform(put(CartItemController.BASE_URL + "/" + 1L + "/" + itemRequest.quantity()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().doubleValue())))
                .andExpect(jsonPath("$.items.size()", is(1)));
    }

    @Test
    void modifyCartItem_badRequest() throws Exception {
        doThrow(new InsufficientStockException()).when(cartItemService).modifyCartItem(1L, itemRequest.quantity());

        mockMvc.perform(put(CartItemController.BASE_URL + "/" + 1L + "/" + itemRequest.quantity()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.INSUFFICIENT_STOCK_MESSAGE)));
    }

    @Test
    void modifyCartItem_notFound() throws Exception {
        doThrow(new CartItemNotFoundException()).when(cartItemService).modifyCartItem(1L, itemRequest.quantity());

        mockMvc.perform(put(CartItemController.BASE_URL + "/" + 1L + "/" + itemRequest.quantity()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CART_ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void removeItemFromCart_ok() throws Exception {
        given(cartItemService.removeItemFromCart(2L)).willReturn(cartDTO);

        mockMvc.perform(delete(CartItemController.BASE_URL + "/" + 2L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().doubleValue())))
                .andExpect(jsonPath("$.items.size()", is(1)));
    }

    @Test
    void removeItemFromCart_notFound() throws Exception {
        doThrow(new CartItemNotFoundException()).when(cartItemService).removeItemFromCart(2L);

        mockMvc.perform(delete(CartItemController.BASE_URL + "/" + 2L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CART_ITEM_NOT_FOUND_MESSAGE)));
    }

    @Test
    void clearActiveCart_ok() throws Exception {
        given(cartItemService.clearActive()).willReturn(cartDTO);

        mockMvc.perform(delete(CartItemController.BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().doubleValue())))
                .andExpect(jsonPath("$.items.size()", is(1)));
    }
}
