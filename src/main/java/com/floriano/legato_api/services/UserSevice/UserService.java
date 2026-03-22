package com.floriano.legato_api.services.UserSevice;

import com.floriano.legato_api.dto.ConnectionDTO.ConnectionRequestResponseDTO;
import com.floriano.legato_api.mapper.connection.ConnectionRequestMapper;
import com.floriano.legato_api.model.Connection.ConnectionRequest;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.AuxiliaryEntity.Location;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.services.UserSevice.useCases.*;
import com.floriano.legato_api.repositories.UserRepository;
import com.floriano.legato_api.dto.UserDTO.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ListAllUsersService listAllUsersService;
    private final FindByEmailService findByEmailService;
    private final FindByIdService findByIdService;
    private final UpdateUserService updateUserService;
    private final DeleteUserService deleteUserService;
    private final FollowUserService followUserService;
    private final UnfollowUserService unfollowUserService;
    private final SendConnectionRequestService sendConnectionRequestService;
    private final AcceptConnectionRequestService acceptConnectionRequestService;
    private final RejectConnectionRequestService rejectConnectionRequestService;
    private final RemoveConnectionService removeConnectionService;
    private final BlockUserService blockUserService;
    private final UnblockUserService unblockUserService;
    private final ListBlockedUsersService listBlockedUsersService;
    private final ListConnectionsService listConnectionsService;
    private final ListFollowersService listFollowersService;
    private final UpdateUserImageService UpdateUserImageService;
    private final UpdateUserCardsImageService UpdateUserCardsImageService;
    private final FindLocationOfUsersService findLocationOfUsersService;
    private final FindUserByUsername findUserByUsername;
    private final RemoveUserCardImageService removeUserCardImageService;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // CRUD

    public List<UserListDTO> listAllUsers() {
        return listAllUsersService.execute();
    }

    public User findByEmail(String email) {
        return findByEmailService.execute(email);
    }

    public User findById(Long id) {
        return findByIdService.execute(id);
    }

    public UserResponseDTO findUserByUsername(String username) {
        return findUserByUsername.execute(username, true);
    }

    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        return updateUserService.execute(id, dto);
    }

    public void deleteUser(Long id) {
        deleteUserService.execute(id);
    }

    public UserResponseDTO updateImg(Long id, MultipartFile file, String imageType) {
        return UpdateUserImageService.execute(id, file, imageType);
    }

    public UserResponseDTO updateCardImgs(Long id, MultipartFile file, int index) {
        return UpdateUserCardsImageService.execute(id, file, index);
    }

    public UserResponseDTO removeCardImage(Long userId, int index) {
        return removeUserCardImageService.execute(userId, index);
    }

    // FOLLOW

    public UserResponseDTO followUser(Long followerId, Long targetId) {
        return followUserService.execute(followerId, targetId);
    }

    public UserResponseDTO unfollowUser(Long followerId, Long targetId) {
        return unfollowUserService.execute(followerId, targetId);
    }

    // CONEXÕES

    public UserResponseDTO sendConnectionRequest(Long senderId, Long receiverId, String message) {
        return sendConnectionRequestService.execute(senderId, receiverId, message);
    }

    public void acceptConnectionRequest(Long receiverId, Long requestId) {
        acceptConnectionRequestService.execute(receiverId, requestId);
    }

    public void rejectConnectionRequest(Long receiverId, Long requestId) {
        rejectConnectionRequestService.execute(receiverId, requestId);
    }

    public UserResponseDTO removeConnection(Long userId, Long targetId) {
        return removeConnectionService.execute(userId, targetId);
    }

    public List<UserResponseDTO> listConnections(Long userId) {
        return listConnectionsService.execute(userId);
    }

    public List<UserListDTO> listFollowers(Long userId) {
        return listFollowersService.execute(userId);
    }

    public List<ConnectionRequestResponseDTO> listSentRequests(Long userId) {
        User user = findById(userId);

        List<ConnectionRequest> connectionRequests =  user.getSentRequests();

        return connectionRequests.stream()
                .map(ConnectionRequestMapper::toDTO)
                .toList();
    }

    public List<ConnectionRequestResponseDTO> listReceivedRequests(Long userId) {
        User user = findById(userId);
        List<ConnectionRequest> connectionRequests = user.getReceivedRequests();

        return connectionRequests.stream()
                .map(ConnectionRequestMapper::toDTO)
                .toList();
    }

    // BLOCK

    public UserResponseDTO blockUser(Long blockerId, Long targetId) {
        return blockUserService.execute(blockerId, targetId);
    }

    public UserResponseDTO unblockUser(Long blockerId, Long targetId) {
        return unblockUserService.execute(blockerId, targetId);
    }

    public List<UserResponseDTO> listBlockedUsers(Long userId) {
        return listBlockedUsersService.execute(userId);
    }

    // GEOLOCALIZATION
    public List<LocationUserDTO> findLocationOfUsers(Long userId, double radiusKm) {
        return findLocationOfUsersService.execute(userId, radiusKm);
    }

    public void updateUserLocation(Long userId, double latitude, double longitude) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (user.getLocation() == null) {
            user.setLocation(new Location());
        }
        
        user.getLocation().setLatitude(latitude);
        user.getLocation().setLongitude(longitude);
        user.getLocation().setUpdatedAt(LocalDateTime.now());
        
        userRepository.save(user);
    }
}
