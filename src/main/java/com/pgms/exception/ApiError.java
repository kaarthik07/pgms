package com.pgms.exception;

import java.time.OffsetDateTime;
import java.util.StringJoiner;

public class ApiError {
    public String path;
    public String message;
    public int status;
    public OffsetDateTime timestamp = OffsetDateTime.now();

    @Override
    public String toString() {
        return new StringJoiner(", ", "ApiError{", "}")
                .add("path='" + path + "'").add("message='" + message + "'")
                .add("status=" + status)
                .add("timestamp=" + timestamp)
                .toString();
    }
}
