package com.app.global.mappers;

import com.app.global.dtos.MediaDTO;
import com.app.global.vos.Media;

public class MediaMapper {

    private MediaMapper() {
    }

    public static MediaDTO toMediaDTO(Media media) {
        return new MediaDTO(
                media.title(),
                media.url(),
                media.format().toString()
        );
    }
}
