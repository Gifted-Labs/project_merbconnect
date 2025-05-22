package com.merbsconnect.academics.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@DiscriminatorValue("BOOK")
public class ReferenceMaterial extends Resource {

    private String fileUrl;
    private String author;
    private String publisher;
    private String isbn;
    private String edition;
    private String publicationYear;
    private String language;
    private String numberOfPages;
    private String format; // e.g., PDF, EPUB, etc.
    private String fileSize; // e.g., 2MB
    private String downloadLink; // URL to download the book
    private String coverImageUrl; // URL to the book cover image
}
