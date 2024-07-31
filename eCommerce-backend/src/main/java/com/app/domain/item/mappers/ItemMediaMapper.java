package com.app.domain.item.mappers;

import com.app.domain.item.dtos.ItemMediaDTO;
import com.app.domain.item.entities.ItemMedia;

import static com.app.global.mappers.MediaMapper.toMediaDTO;

public class ItemMediaMapper {

    private ItemMediaMapper() {
    }

    public static ItemMediaDTO toItemMediaDTO(ItemMedia media) {
        return new ItemMediaDTO(
                media.getId(),
                toMediaDTO(media.getMedia()),
                media.getAltText()
        );
    }
}
