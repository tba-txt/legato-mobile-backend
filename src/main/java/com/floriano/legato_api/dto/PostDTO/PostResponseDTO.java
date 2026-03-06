package com.floriano.legato_api.dto.PostDTO;

import com.floriano.legato_api.model.Post.TypeMedia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {
    private Long id;
    private String content;

    private TypeMedia typeMedia;
    private String urlMedia;

    private int likes;
    private int commentsNumber;

    private Long userId;
    private String username;
    private String displayName;

    private LocalDateTime createdAt;
}
