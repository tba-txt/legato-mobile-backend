package com.floriano.legato_api.services.ColaborationService;

import com.floriano.legato_api.dto.ColaborationDTO.ColaborationRequestDTO;
import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.ColaborationRepository;
import com.floriano.legato_api.services.UserSevice.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColaborationServiceTest {

    @Mock
    private ColaborationRepository colaborationRepository;
    @Mock
    private UserService userService;
    @InjectMocks
    private ColaborationService colaborationService;

    @Test
    void createColaboration_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        ColaborationRequestDTO requestDTO = new ColaborationRequestDTO();
        requestDTO.setTitle("New Collab");

        when(userService.findById(userId)).thenReturn(user);
        when(colaborationRepository.save(any(Colaboration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Colaboration result = colaborationService.createColaboration(requestDTO, userId);

        assertNotNull(result);
        assertEquals("New Collab", result.getTitle());
        assertEquals(user, result.getAuthor());
    }
}
