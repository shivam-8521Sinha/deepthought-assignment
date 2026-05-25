package com.employee.management.exception;

import com.employee.management.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (ex.getErrorCode().contains("NOT_FOUND")) {
            status = HttpStatus.NOT_FOUND;
        }

        if (ex.getErrorCode().contains("DUPLICATE")) {
            status = HttpStatus.CONFLICT;
        }

        ErrorResponse response =
                new ErrorResponse(ex.getErrorCode(), ex.getMessage());

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

        ErrorResponse response =
                new ErrorResponse(
                        "INTERNAL_SERVER_ERROR",
                        ex.getMessage()
                );

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}