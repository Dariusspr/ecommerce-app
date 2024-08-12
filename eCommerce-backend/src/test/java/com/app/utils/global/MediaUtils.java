package com.app.utils.global;

import com.app.global.enums.FileFormat;
import com.app.global.vos.Media;
import org.apache.commons.lang3.RandomStringUtils;


import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MAX;
import static com.app.global.constants.UserInputConstants.TITLE_LENGTH_MIN;

public class MediaUtils {
    private static final int URL_LENGTH = 90;

    private Media media;

    public static Media getMedia() {
        FileFormat format = getFileFormat();
        return getMedia(format);
    }

    public static Media getMedia(FileFormat fileFormat) {
        String title = getTitle();
        String url = getUrl();
        return new Media(title, url ,fileFormat);
    }

    private static FileFormat getFileFormat() {
        FileFormat[] formats = FileFormat.values();
        return formats[NumberUtils.getIntegerInRange(0, formats.length)];
    }

    private static String getTitle() {
        return RandomStringUtils.randomAlphanumeric(TITLE_LENGTH_MIN, TITLE_LENGTH_MAX);
    }

    private static String getUrl() {
        return RandomStringUtils.randomAlphanumeric(URL_LENGTH);
    }

    private MediaUtils() {}
}
