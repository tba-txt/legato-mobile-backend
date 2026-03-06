package com.floriano.legato_api.services.PostService.Utils;

import com.floriano.legato_api.model.Post.TypeMedia;
import org.springframework.web.multipart.MultipartFile;

public class DetermineMediaType {


    public static TypeMedia determineMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) return null;

        if (contentType.startsWith("image/")) return TypeMedia.IMAGE;
        if (contentType.startsWith("video/")) return TypeMedia.VIDEO;
        if (contentType.startsWith("audio/")) return TypeMedia.AUDIO;

        return null;
    }
}
