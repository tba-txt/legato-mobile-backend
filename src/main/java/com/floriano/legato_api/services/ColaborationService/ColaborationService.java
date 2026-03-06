package com.floriano.legato_api.services.ColaborationService;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationResponseDTO;
import com.floriano.legato_api.dto.ColaborationDTO.ColaborationUpdateDTO;
import com.floriano.legato_api.services.ColaborationService.useCases.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ColaborationService {


    private final CreateColaborationService createColaborationService;
    private final UpdateColaborationService updateColaborationService;
    private final DeleteColaborationService deleteColaborationService;
    private final FindColaborationByIdService findColaborationByIdService;
    private final UploadColaborationImageService uploadColaborationImageService;
    private final ListByUserColaborationService listByUserColaboration;
    private final ListAllColaborationsService listAllColaborationsService;

    public List<ColaborationResponseDTO> listColaborations() {
        return listAllColaborationsService.execute();
    }

    public ColaborationResponseDTO createColaboration(Long userId, ColaborationRequestDTO dto) {
        return createColaborationService.execute(userId, dto);
    }

    public ColaborationResponseDTO updateColaboration(Long id, ColaborationUpdateDTO dto) {
        return updateColaborationService.execute(id, dto);
    }

    public ColaborationResponseDTO updateColaborationimage(Long id, MultipartFile file) {
        return uploadColaborationImageService.execute(id, file);
    }

    public List<ColaborationResponseDTO> listByUserColaboration(Long userid) {
        return listByUserColaboration.execute(userid);
    }

    public void deleteColaboration(Long id) {
        deleteColaborationService.execute(id);
    }

    public ColaborationResponseDTO findById(Long id) {
        return findColaborationByIdService.execute(id);
    }

}
