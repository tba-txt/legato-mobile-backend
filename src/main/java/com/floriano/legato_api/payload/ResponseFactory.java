package com.floriano.legato_api.payload;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, data));
    }

    public static ResponseEntity<ApiResponse<Void>> ok(String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return new ResponseEntity<>(new ApiResponse<>(true, message, data), HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return new ResponseEntity<>(new ApiResponse<>(false, message, null), HttpStatus.NOT_FOUND);
    }
}