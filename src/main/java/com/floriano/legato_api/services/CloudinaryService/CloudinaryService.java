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
        if (fileUrl == null || fileUrl.trim().isEmpty() || fileUrl.equals("string")) {
            return; 
        }

        try {
            String publicId = extractPublicId(fileUrl);
            if (publicId != null) {
                
                Map<String, Object> options = new HashMap<>();
                
            if (fileUrl.toLowerCase().matches(".*\\.(mp4|mov|avi|webm|mkv|mp3|wav|ogg|flac)$")) {
                options.put("resource_type", "video");
            } else {
                options.put("resource_type", "image");
            }

                cloudinary.uploader().destroy(publicId, options);
            }
        } catch (Exception e) {
            System.err.println("Aviso: Falha ao deletar arquivo no Cloudinary: " + fileUrl);
        }
    }

    private String extractPublicId(String url) {
            try {
                String[] parts = url.split("/");
                if (parts.length < 2) return null; 

                String filename = parts[parts.length - 1];
                String folder = parts[parts.length - 2];
                
                int dotIndex = filename.lastIndexOf(".");
                if (dotIndex == -1) {
                    return folder + "/" + filename; 
                }
                
                return folder + "/" + filename.substring(0, dotIndex);
            } catch (Exception e) {
                return null; 
            }
        }
    }