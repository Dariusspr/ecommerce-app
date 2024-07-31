package com.app.domain.item.dtos;

import com.app.global.dtos.MediaDTO;

public record ItemMediaDTO(Long id, MediaDTO media, String altText) {
}
