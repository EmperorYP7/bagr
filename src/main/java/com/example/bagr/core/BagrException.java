package com.example.bagr.core;

import com.example.bagr.view.Status;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BagrException extends Exception {
    public enum Reason {
        ALL_OK,
        INTERNAL_SERVER_ERROR,
        DATABASE_ERROR,
        NOT_AUTHORISED,
        BAD_REQUEST,
        NOT_FOUND
    }

    private int code;
    private String message;
    private Reason reason;

    public Status toStatus() {
        return Status.builder()
                    .message(message)
                    .code(code)
                    .reason(reason.toString())
                .build();
    }
}
