package com.app.domain.cart.dtos;

import com.app.domain.item.dtos.ItemSummaryDTO;

import java.math.BigDecimal;

public record CartItemDTO(
        Long cardId,
        ItemSummaryDTO itemSummary,
        int quantity) {
}