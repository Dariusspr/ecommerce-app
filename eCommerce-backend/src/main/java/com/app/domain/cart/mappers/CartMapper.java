package com.app.domain.cart.mappers;


import com.app.domain.cart.dtos.CartDTO;
import com.app.domain.cart.dtos.CartItemDTO;
import com.app.domain.cart.entities.Cart;
import com.app.domain.cart.entities.CartItem;

import java.util.List;

public class CartMapper {

    public static CartDTO toCartDto(Cart cart) {
        List<CartItem> items = cart.getCartItems();
        List<CartItemDTO> itemDTOs = items.stream()
                .map(CartItemMapper::toCartItemDto)
                .toList();
        return new CartDTO(cart.getId(),
                itemDTOs,
                cart.getTotalCost());
    }

}