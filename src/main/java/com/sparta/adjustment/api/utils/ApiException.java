package com.sparta.adjustment.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.sparta.adjustment.api")
public class ApiException {
    //exception 추가 해야함
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<?>> allExceptions(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<?>> handleValidationExceptions(BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CommonApiResponse.fail(bindingResult));
    }
}
