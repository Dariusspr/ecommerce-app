package com.app.global.exceptions;

import com.app.global.constants.ExceptionMessages;

public class FailedToUploadFileException extends RuntimeException {
    public FailedToUploadFileException() {
        super(ExceptionMessages.FAILED_TO_UPLOAD_FILE_MESSAGE);
    }
}
