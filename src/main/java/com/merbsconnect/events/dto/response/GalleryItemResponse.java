package com.merbsconnect.events.dto.response;

import com.merbsconnect.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for a gallery item.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryItemResponse implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long eventId;
    private String mediaUrl;
    private String caption;
    private MediaType type;
    private String fileName;
    private Long fileSize;
    private LocalDateTime createdAt;
}
