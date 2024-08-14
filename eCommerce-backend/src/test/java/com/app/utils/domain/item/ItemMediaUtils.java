package com.app.utils.domain.item;

import com.app.domain.item.entities.ItemMedia;
import com.app.global.vos.Media;
import com.app.utils.global.MediaUtils;
import com.app.utils.global.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemMediaUtils {
    private static final int ALT_TEXT_LENGTH_MAX = 255;

    public static List<ItemMedia> getItemMedia(int count) {
        List<ItemMedia> itemMediaList = new ArrayList<>();
        for (int i  = 0; i < count; i++) {
            itemMediaList.add(getItemMedia());
        }
        return itemMediaList;
    }

    public static ItemMedia getItemMedia() {
       Media media = MediaUtils.getMedia();
       String altText = StringUtils.getText(ALT_TEXT_LENGTH_MAX);
        return new ItemMedia(media, altText);
    }

    private ItemMediaUtils() {}
}
