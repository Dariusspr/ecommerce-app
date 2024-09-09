package com.app.domain.cart.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartDTO(UUID cartId, List<CartItemDTO> items, BigDecimal totalCost) {
}
