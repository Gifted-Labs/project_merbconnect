package com.merbsconnect.events.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for event gallery including items and optional Google Drive
 * link.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GalleryResponse implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private String eventTitle;
    private String googleDriveFolderLink;
    private List<GalleryItemResponse> items;
    private long totalItems;
}
