package com.floriano.legato_api.services.CloudinaryService;

import com.cloudinary.Cloudinary;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

    public String generateSignedDownloadUrl(String mediaUrl) {
        if (mediaUrl == null) return null;
        return mediaUrl.replace("/upload/", "/upload/fl_attachment/");
    }

    public byte[] downloadFileDirectly(String mediaUrl) throws Exception {
        if (mediaUrl == null) throw new RuntimeException("URL de mídia nula");
        URL url = new URL(mediaUrl);
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }

    public byte[] downloadFileBytes(String mediaUrl) throws Exception {
        String resourceType = extractResourceType(mediaUrl);
        String publicId = extractFullPublicId(mediaUrl);
        if (publicId == null) throw new RuntimeException("Não foi possível extrair o public ID de: " + mediaUrl);

        String signedUrl = cloudinary.url()
                .resourceType(resourceType)
                .type("upload")
                .signed(true)
                .generate(publicId);

        URL url = new URL(signedUrl);
        try (InputStream in = url.openStream()) {
            return in.readAllBytes();
        }
    }

    private String extractResourceType(String url) {
        if (url.contains("/video/upload/")) return "video";
        if (url.contains("/raw/upload/")) return "raw";
        return "image";
    }

    private String extractFullPublicId(String url) {
        try {
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("/upload/(?:v\\d+/)?(.+)")
                    .matcher(url);
            if (matcher.find()) {
                String withExt = matcher.group(1);
                int dot = withExt.lastIndexOf('.');
                return dot > 0 ? withExt.substring(0, dot) : withExt;
            }
            return null;
        } catch (Exception e) {
            return null;
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
                
            String lowerUrl = fileUrl.toLowerCase();
            if (lowerUrl.matches(".*\\.(mp4|mov|avi|webm|mkv|mp3|wav|ogg|flac)$")) {
                options.put("resource_type", "video");
            } else if (lowerUrl.matches(".*\\.(pdf|doc|docx|xls|xlsx|ppt|pptx|txt|csv|zip)$")) {
                options.put("resource_type", "raw");
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