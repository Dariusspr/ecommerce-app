package com.app.domain.cart.mappers;

import com.app.domain.cart.dtos.CartItemDTO;
import com.app.domain.cart.entities.CartItem;
import com.app.domain.item.dtos.ItemSummaryDTO;
import com.app.domain.item.entities.Item;
import com.app.domain.item.mappers.ItemMapper;

public class CartItemMapper {

    public static CartItemDTO toCartItemDto(CartItem cartItem) {
        Item item = cartItem.getItem();
        ItemSummaryDTO itemSummary = ItemMapper.toItemSummaryDTO(item);
        return new CartItemDTO(
                cartItem.getId(),
                itemSummary,
                cartItem.getQuantity(),
                cartItem.getPricePerUnit());
    }
}