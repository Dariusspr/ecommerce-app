package com.app.domain.cart.controllers.members;


import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.exceptions.CartNotFoundException;
import com.app.domain.cart.services.CartService;
import com.app.global.config.security.JwtAuthenticationFilter;
import com.app.global.constants.ExceptionMessages;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = CartController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private CartDTO cartDTO;

    @BeforeAll
    void setup() {
        cartDTO = new CartDTO(UUID.randomUUID(), Collections.emptyList(), BigDecimal.ZERO);
    }

    @Test
    void getActiveCart_ok() throws Exception {
        given(cartService.getActiveCartDto()).willReturn(cartDTO);

        mockMvc.perform(get(CartController.BASE_URL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().intValue())))
                .andExpect(jsonPath("$.items.size()", is(0)));
    }

    @Test
    void getCartById_ok() throws Exception {
        given(cartService.findDtoById(any())).willReturn(cartDTO);

        mockMvc.perform(get(CartController.BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId", is(cartDTO.cartId().toString())))
                .andExpect(jsonPath("$.totalCost", is(cartDTO.totalCost().intValue())))
                .andExpect(jsonPath("$.items.size()", is(0)));
    }

    @Test
    void getActiveCart_notFound() throws Exception {
        doThrow(new CartNotFoundException()).when(cartService).findDtoById(any());

        mockMvc.perform(get(CartController.BASE_URL + "/" + UUID.randomUUID()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(ExceptionMessages.CART_NOT_FOUND_MESSAGE)));
    }
}
