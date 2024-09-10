package com.app.domain.item.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ItemDetailedDTO(UUID id,
                            String title,
                            BigDecimal price,
                            String description,
                            int quantity,
                            boolean active,
                            MemberSummaryDTO seller,
                            List<ItemMediaDTO> media,
                            String category,
                            LocalDateTime createdDate,
                            LocalDateTime lastModifiedData) {
}
