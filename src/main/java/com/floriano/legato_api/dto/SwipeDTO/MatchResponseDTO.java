package com.floriano.legato_api.dto.SwipeDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Esconde nulos (se não der match, não manda o id)
public class MatchResponseDTO {
    private boolean match;
    private Long conversationId;
}