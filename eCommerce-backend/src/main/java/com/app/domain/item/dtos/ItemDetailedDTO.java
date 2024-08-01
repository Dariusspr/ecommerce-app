package com.app.domain.item.dtos;

import com.app.domain.member.dtos.MemberSummaryDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ItemDetailedDTO(Long id, String title,
                              BigDecimal price, String description,
                              MemberSummaryDTO seller, List<ItemMediaDTO> media,
                              String category, LocalDateTime createdDate,
                              LocalDateTime lastModifiedData){
}
