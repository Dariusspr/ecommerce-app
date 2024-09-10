package com.app.domain.item.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ItemSummaryDTO(
        UUID id,
        String title,
        BigDecimal price,
        boolean active,
        List<ItemMediaDTO> media) {
}
