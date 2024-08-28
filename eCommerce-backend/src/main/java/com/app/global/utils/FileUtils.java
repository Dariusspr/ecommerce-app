package com.app.global.utils;

import com.app.global.enums.FileFormat;
import com.app.global.exceptions.UnsupportedFileFormatException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.EnumUtils;

import java.util.Objects;

public class FileUtils {

    public static String getValidatedFileFormat(String name) {
        final String format = Objects.requireNonNull(FilenameUtils.getExtension(name)).toUpperCase();
        if (!EnumUtils.isValidEnum(FileFormat.class, format)) {
            throw new UnsupportedFileFormatException();
        }
        return format;
    }
}
