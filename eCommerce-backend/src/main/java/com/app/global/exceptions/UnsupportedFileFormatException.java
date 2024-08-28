package com.app.global.exceptions;

import com.app.global.constants.ExceptionMessages;

public class UnsupportedFileFormatException extends RuntimeException{
    public UnsupportedFileFormatException() {
        super(ExceptionMessages.UNSUPPORTED_FILE_FORMAT_MESSAGE);
    }
}
