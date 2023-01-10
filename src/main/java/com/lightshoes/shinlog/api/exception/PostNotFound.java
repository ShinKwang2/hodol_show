package com.lightshoes.shinlog.api.exception;

/**
 * status -> 404
 */
public class PostNotFound extends ShinlogException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
