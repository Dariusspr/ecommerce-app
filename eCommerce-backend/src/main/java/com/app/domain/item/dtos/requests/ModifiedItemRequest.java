package com.app.domain.item.dtos.requests;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public record ModifiedItemRequest(String title, BigDecimal price, String description, List<MultipartFile> media,
                                  Long categoryId) {
}
