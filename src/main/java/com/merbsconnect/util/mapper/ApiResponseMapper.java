package com.merbsconnect.util.mapper;

import com.merbsconnect.academics.dto.response.ApiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for creating standardized API responses.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponseMapper {

    /**
     * Creates a success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates a success response with a custom message and data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with a message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates an error response with a message and error data
     */
    public static <T> ApiResponse<T> error(String message, T errorData) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(errorData)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Maps a Page to an ApiResponse with transformed content
     */
    public static <T, R> ApiResponse<List<R>> mapPageToApiResponse(
            Page<T> page,
            Function<T, R> mapper,
            String message) {

        List<R> mappedContent = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return success(message, mappedContent);
    }
}