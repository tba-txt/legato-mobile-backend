package com.floriano.legato_api.dto.SwipeDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.floriano.legato_api.model.User.enums.InstrumentList;
import com.floriano.legato_api.model.User.enums.Genre;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SwipeHistoryEntryDTO {
    private Long id;
    private String displayName;
    private String profilePicture;
    private List<InstrumentList> instruments;
    private List<Genre> genres;
    private String bio;
    private List<String> photosCard;
    private String direction;
    private LocalDateTime createdAt;
}