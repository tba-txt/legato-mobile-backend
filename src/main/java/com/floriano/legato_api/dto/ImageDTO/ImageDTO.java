package com.floriano.legato_api.dto.ImageDTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageDTO {
    private String name;
    private MultipartFile file;
}
