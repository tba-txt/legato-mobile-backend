package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.exceptions.RecursoNaoEncontradoException;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowUserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private FollowUserService followUserService;

    @Test
    void execute_Success() {
        User follower = new User();
        follower.setId(1L);
        follower.setFollowing(new HashSet<>());

        User target = new User();
        target.setId(2L);
        target.setFollowers(new HashSet<>());

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.of(target));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User result = followUserService.execute(1L, 2L);

        assertNotNull(result);
        assertTrue(follower.getFollowing().contains(target));
        assertTrue(target.getFollowers().contains(follower));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void execute_FollowerNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            followUserService.execute(1L, 2L);
        });
    }

    @Test
    void execute_TargetNotFound() {
        User follower = new User();
        follower.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(follower));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RecursoNaoEncontradoException.class, () -> {
            followUserService.execute(1L, 2L);
        });
    }
}
