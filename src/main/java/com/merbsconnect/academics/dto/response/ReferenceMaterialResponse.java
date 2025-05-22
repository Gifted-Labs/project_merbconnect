package com.merbsconnect.academics.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceMaterialResponse {
    private Long id;
    private String title;
    private String description;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private String fileUrl;
    private String author;
    private String publisher;
    private String isbn;
    private String edition;
    private String publicationYear;
    private String language;
    private String numberOfPages;
    private String format;
    private String fileSize;
    private String downloadLink;
    private String coverImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}