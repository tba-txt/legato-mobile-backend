package com.floriano.legato_api.dto.SwipeDTO;
import lombok.Data;

@Data
public class SwipeRequestDTO {
    private Long swipedId; // O ID do carinha que apareceu no card
    private boolean isLike; // true pra coração, false pra Xis
}