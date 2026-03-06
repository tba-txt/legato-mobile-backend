package com.floriano.legato_api.services.CloudinaryService;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

    @Resource
    private Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folderName) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", folderName);
            options.put("resource_type", "auto");

            Map uploadedFile = cloudinary.uploader().upload(file.getBytes(), options);

            return (String) uploadedFile.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload no Cloudinary", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String publicId = extractPublicId(fileUrl);
            cloudinary.uploader().destroy(publicId, Map.of());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar imagem do Cloudinary", e);
        }
    }

    private String extractPublicId(String url) {
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1];
        String folder = parts[parts.length - 2];
        return folder + "/" + filename.substring(0, filename.lastIndexOf("."));
    }

}
