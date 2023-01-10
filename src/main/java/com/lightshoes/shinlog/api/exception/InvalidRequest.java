package com.lightshoes.shinlog.api.exception;

import lombok.Getter;

/**
 * status -> 400
 */
@Getter
public class InvalidRequest extends ShinlogException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequest(String fieldName, String errorMessage) {
        super(MESSAGE);
        addValidation(fieldName, errorMessage);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
