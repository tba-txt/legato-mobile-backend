package com.floriano.legato_api.services.PostService.Utils;

import com.floriano.legato_api.model.Post.TypeMedia;
import org.springframework.web.multipart.MultipartFile;

public class DetermineMediaType {


    private static final java.util.Set<String> DOCUMENT_TYPES = java.util.Set.of(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint",
        "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain",
        "text/csv",
        "application/zip",
        "application/x-zip-compressed"
    );

    public static TypeMedia determineMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return null;

        if (contentType.startsWith("image/")) return TypeMedia.IMAGE;
        if (contentType.startsWith("video/")) return TypeMedia.VIDEO;
        if (contentType.startsWith("audio/")) return TypeMedia.AUDIO;
        if (DOCUMENT_TYPES.contains(contentType)) return TypeMedia.FILE;

        return null;
    }
}
