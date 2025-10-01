package com.pgms.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.validation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> nf(NotFoundException ex, HttpServletRequest req) {
        return build(req, ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({BadRequestException.class, MethodArgumentNotValidException.class, BindException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> bad(Exception ex, HttpServletRequest req) {
        return build(req, ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> generic(Exception ex, HttpServletRequest req) {
        log.error("Unexpected", ex);
        return build(req, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiError> build(HttpServletRequest req, String msg, HttpStatus st) {
        ApiError e = new ApiError();
        e.path = req.getRequestURI();
        e.message = msg;
        e.status = st.value();
        return ResponseEntity.status(st).body(e);
    }
}
