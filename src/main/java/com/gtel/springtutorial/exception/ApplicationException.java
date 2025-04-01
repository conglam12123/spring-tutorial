package com.gtel.springtutorial.exception;

import com.gtel.springtutorial.utils.ERROR_CODE;

import java.util.Map;

public class ApplicationException extends RuntimeException{
    private String code;
    private Map<String, Object> data;
    private String title;

    public ApplicationException(ERROR_CODE errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getErrorCode();
        this.title = errorCode.getMessage();
    }
    public ApplicationException(ERROR_CODE errorCode, String message) {
        super(message);
        this.code = errorCode.getErrorCode();
        this.title = message;
    }
}
