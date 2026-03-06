package com.floriano.legato_api.mapper.connection;

import com.floriano.legato_api.dto.ConnectionDTO.ConnectionRequestResponseDTO;
import com.floriano.legato_api.model.Connection.ConnectionRequest;

public class ConnectionRequestMapper {

    public static ConnectionRequestResponseDTO toDTO(ConnectionRequest request) {
        if (request == null) return null;

        ConnectionRequestResponseDTO dto = new ConnectionRequestResponseDTO();
        dto.setId(request.getId());
        dto.setSenderId(request.getSender() != null ? request.getSender().getId() : null);
        dto.setSenderUsername(request.getSender() != null ? request.getSender().getUsername() : null);
        dto.setReceiverId(request.getReceiver() != null ? request.getReceiver().getId() : null);
        dto.setReceiverUsername(request.getReceiver() != null ? request.getReceiver().getUsername() : null);
        dto.setMessage(request.getMessage());
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        dto.setCreatedAt(request.getCreatedAt());
        return dto;
    }
}
