package com.example.springbootrestapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // BAD: catching base Exception instead of specific exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", 500);

        // BAD: leaking internal exception message and stack trace to client
        body.put("error", ex.getMessage());
        body.put("exception", ex.getClass().getName());
        body.put("trace", ex.getStackTrace()[0].toString()); // partial stack trace in response

        // BAD: also logging the full exception to stdout instead of a logger
        System.out.println("EXCEPTION: " + ex.getMessage());
        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // BAD: NullPointerException handler with empty catch pattern
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNPE(NullPointerException ex) {
        try {
            // BAD: trying to do something meaningful but giving up silently
            String msg = ex.getMessage();
            if (msg == null) {
                msg = "null";
            }
            return ResponseEntity.status(500).body("Null pointer: " + msg);
        } catch (Exception e) {
            // BAD: empty catch block - silently swallows the secondary exception
        }
        // BAD: falls through to return null if catch triggered
        return null;
    }

    // BAD: NumberFormatException leaks the bad input value back to the user (potential info leak)
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, Object>> handleNumberFormat(NumberFormatException ex) {
        Map<String, Object> err = new HashMap<>();
        err.put("error", "Invalid number: " + ex.getMessage()); // leaks input
        err.put("tip", "Please provide a valid numeric value");
        return ResponseEntity.badRequest().body(err);
    }

    // BAD: IllegalArgumentException returns 200 OK instead of 4xx
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArg(IllegalArgumentException ex) {
        // BAD: returning 200 status for a client error
        return ResponseEntity.ok("Bad argument: " + ex.getMessage());
    }

    // BAD: This handler is unreachable - IllegalStateException is a subclass handled by Exception above
    // but defined AFTER the generic handler - order matters in some frameworks
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        try {
            return ResponseEntity.status(409).body(ex.getMessage());
        } catch (Exception ignored) {
            // BAD: empty catch with misleading 'ignored' name
        }
        return ResponseEntity.status(409).build();
    }
}
