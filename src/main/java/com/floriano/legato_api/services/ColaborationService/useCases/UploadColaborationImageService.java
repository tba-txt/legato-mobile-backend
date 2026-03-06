package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.mapper.ColaborationMapper.ColaborationMapper;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.repositories.ColaborationRepository;
import com.floriano.legato_api.services.CloudinaryService.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UploadColaborationImageService {

    private final ColaborationRepository colaborationRepository;
    private final CloudinaryService cloudinaryService;

    public ColaborationResponseDTO execute(Long colabId, MultipartFile file) {

        Colaboration colab = colaborationRepository.findById(colabId)
                .orElseThrow(() -> new RuntimeException("Colaboration not found"));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String imageUrl = cloudinaryService.uploadFile(file, "colaborations");
        colab.setImageUrl(imageUrl);

        colaborationRepository.save(colab);

        return ColaborationMapper.toResponse(colab);
    }
}
