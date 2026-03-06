package com.floriano.legato_api.dto.ConnectionDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestCreateDTO {
    private Long receiverId;
    private String message;
}
