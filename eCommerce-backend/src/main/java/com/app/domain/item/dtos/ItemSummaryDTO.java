package com.app.domain.item.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ItemSummaryDTO(Long id, String title,
                             BigDecimal price, List<ItemMediaDTO> media) {
}
