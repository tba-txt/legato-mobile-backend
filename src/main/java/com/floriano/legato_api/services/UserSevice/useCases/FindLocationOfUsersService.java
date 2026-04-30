package com.floriano.legato_api.services.UserSevice.useCases;

import com.floriano.legato_api.dto.UserDTO.LocationUserDTO;
import com.floriano.legato_api.mapper.user.LocationUserMapper;
import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FindLocationOfUsersService {

    private final UserRepository userRepository;
    private final ListAllUsersService listAllUsersService;

    public List<LocationUserDTO> execute(Long userId, double radiusKm) {
        User origin = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        double originLat = origin.getLocation().getLatitude();
        double originLon = origin.getLocation().getLongitude();

        // 1. Busca os usuários e já filtra quem não deve aparecer
        List<User> users = listAllUsersService.getRawUsers().stream()
                .filter(u -> !u.getId().equals(userId)) 
                .filter(u -> !origin.getBlockedUsers().contains(u)) 
                .filter(u -> !u.getBlockedUsers().contains(origin)) 
                .filter(u -> u.getLocation() != null)
                .toList();

        // 2. Calcula a distância matemática e retorna os mais próximos
        return users.stream()
                .map(u -> {
                    double dist = haversine(
                            originLat, originLon,
                            u.getLocation().getLatitude(),
                            u.getLocation().getLongitude()
                    );

                    return LocationUserMapper.toDTO(u, dist);
                })
                .filter(dto -> dto.getDistanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(LocationUserDTO::getDistanceKm))
                .collect(Collectors.toList());
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // raio da terra em KM

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // distância em km
    }
}