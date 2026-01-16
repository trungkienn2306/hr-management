package com.kiennt.hrManagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer code;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, 200, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, 200, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null, 200, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(false, message, null, code, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(Integer code, String message, T data) {
        return new ApiResponse<>(false, message, data, code, LocalDateTime.now());
    }
}