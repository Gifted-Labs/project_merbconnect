package com.merbsconnect.academics.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReferenceMaterialRequest {
    
    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String title;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private Long courseId;
    
    @Size(max = 255, message = "File URL must not exceed 255 characters")
    private String fileUrl;
    
    @Size(max = 100, message = "Author must not exceed 100 characters")
    private String author;
    
    @Size(max = 100, message = "Publisher must not exceed 100 characters")
    private String publisher;
    
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "ISBN must be a valid ISBN-10 or ISBN-13 format")
    private String isbn;
    
    @Size(max = 20, message = "Edition must not exceed 20 characters")
    private String edition;
    
    @Pattern(regexp = "^\\d{4}$", message = "Publication year must be a 4-digit year")
    private String publicationYear;
    
    @Size(max = 50, message = "Language must not exceed 50 characters")
    private String language;
    
    @Size(max = 10, message = "Number of pages must not exceed 10 characters")
    private String numberOfPages;
    
    @Size(max = 20, message = "Format must not exceed 20 characters")
    private String format;
    
    @Size(max = 20, message = "File size must not exceed 20 characters")
    private String fileSize;
    
    @Size(max = 255, message = "Download link must not exceed 255 characters")
    private String downloadLink;
    
    @Size(max = 255, message = "Cover image URL must not exceed 255 characters")
    private String coverImageUrl;
}