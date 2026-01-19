package com.kiennt.hrManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer code;
    private String errorCode;
    private LocalDateTime timestamp;

    // Success methods
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .code(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .code(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(null)
                .code(200)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error methods
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error methods with error code
    public static <T> ApiResponse<T> error(Integer code, String errorCode, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .code(code)
                .errorCode(errorCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Convenience methods
    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .code(201)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> conflict(String errorCode, String message) {
        return error(409, errorCode, message);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, "NOT_FOUND", message);
    }

    public static <T> ApiResponse<T> badRequest(String errorCode, String message) {
        return error(400, errorCode, message);
    }
}