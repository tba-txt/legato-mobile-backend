package com.floriano.legato_api.controllers.CloudinaryController;

import com.floriano.legato_api.payload.ApiResponse;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
@Tag(name = "File Upload")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    public CloudinaryController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Operation(summary = "Upload a file to Cloudinary")
    @PostMapping
    public ResponseEntity<ApiResponse<String>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("folder") String folder) {
        String fileUrl = cloudinaryService.uploadFile(file, folder);
        return ResponseEntity.ok(new ApiResponse<>(true, "File uploaded successfully", fileUrl));
    }
}
