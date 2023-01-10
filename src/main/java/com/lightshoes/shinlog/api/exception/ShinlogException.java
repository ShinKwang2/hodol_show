package com.lightshoes.shinlog.api.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public abstract class ShinlogException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public ShinlogException(String message) {
        super(message);
    }

    public ShinlogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String errorMessage) {
        validation.put(fieldName, errorMessage);
    }
}
