package com.merbsconnect.util.mapper;

import com.merbsconnect.dto.response.PageResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class for handling pagination-related operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaginationMapper {

    /**
     * Creates a Pageable object from page, size, and sort parameters
     */
    public static Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        if (sortBy != null && !sortBy.isEmpty()) {
            Sort.Direction direction = sortDirection != null && sortDirection.equalsIgnoreCase("DESC")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    /**
     * Creates a pagination metadata map from a Page object
     */
    public static Map<String, Object> getPageMetadata(Page<?> page) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("currentPage", page.getNumber());
        metadata.put("totalPages", page.getTotalPages());
        metadata.put("totalElements", page.getTotalElements());
        metadata.put("size", page.getSize());
        metadata.put("first", page.isFirst());
        metadata.put("last", page.isLast());
        metadata.put("hasNext", page.hasNext());
        metadata.put("hasPrevious", page.hasPrevious());

        return metadata;
    }

    /**
     * Maps a Page to a list of DTOs using the provided mapper function
     */
    public static <T, R> List<R> mapPageToList(Page<T> page, Function<T, R> mapper) {
        return page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    /**
     * Creates a response map containing both content and pagination metadata
     */
    public static <T, R> Map<String, Object> createPaginatedResponse(Page<T> page, Function<T, R> mapper) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", mapPageToList(page, mapper));
        response.put("pagination", getPageMetadata(page));

        return response;
    }

    /**
     * Creates a default Pageable with sensible defaults
     */
    public static Pageable defaultPageable() {
        return PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Creates a Pageable for searching with a larger page size
     */
    public static Pageable searchPageable(Integer page, Integer size) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 20; // Larger default for search results

        return PageRequest.of(pageNumber, pageSize);
    }

    /**
     * Validates and adjusts page request parameters
     */
    public static Pageable validatePageRequest(Integer page, Integer size, String sortBy, String sortDirection) {
        // Validate page number
        int pageNumber = (page != null && page >= 0) ? page : 0;

        // Validate page size (between 1 and 100)
        int pageSize = 10;
        if (size != null) {
            pageSize = Math.max(1, Math.min(size, 100));
        }

        // Validate sort direction
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortDirection != null && sortDirection.equalsIgnoreCase("DESC")) {
            direction = Sort.Direction.DESC;
        }

        // Create pageable with or without sort
        if (sortBy != null && !sortBy.isEmpty()) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortBy));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    /**
     * Creates a Pageable with multiple sort criteria
     */
    public static Pageable createMultiSortPageable(Integer page, Integer size, Map<String, String> sortCriteria) {
        int pageNumber = page != null ? page : 0;
        int pageSize = size != null ? size : 10;

        if (sortCriteria != null && !sortCriteria.isEmpty()) {
            List<Sort.Order> orders = sortCriteria.entrySet().stream()
                    .map(entry -> {
                        Sort.Direction direction = entry.getValue().equalsIgnoreCase("DESC") ? Sort.Direction.DESC
                                : Sort.Direction.ASC;
                        return new Sort.Order(direction, entry.getKey());
                    })
                    .collect(Collectors.toList());

            return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    /**
     * Maps a Page of entities to a PageResponse DTO using the provided mapper
     * function
     *
     * @param page   the Spring Data Page object
     * @param mapper the function to map each entity to a DTO
     * @param <T>    the entity type
     * @param <R>    the DTO type
     * @return a PageResponse containing the mapped content and pagination metadata
     */
    public static <T, R> PageResponse<R> mapToPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<R>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}