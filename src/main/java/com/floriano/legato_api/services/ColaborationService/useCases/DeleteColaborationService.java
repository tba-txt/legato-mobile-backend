package com.floriano.legato_api.services.ColaborationService.useCases;

import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ColaborationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteColaborationService {

    private final ColaborationRepository colaborationRepository;

    public void execute(Long id) {
        Colaboration colab = colaborationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaboração com id " + id + " não encontrada"));

        User owner = colab.getUser();
        if (owner != null && owner.getColaborations() != null) {
            owner.getColaborations().remove(colab);
        }

        colaborationRepository.delete(colab);
    }
}
