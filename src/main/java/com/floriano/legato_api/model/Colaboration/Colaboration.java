package com.floriano.legato_api.model.Colaboration;

import com.floriano.legato_api.model.User.User;
import com.floriano.legato_api.model.User.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "colaborations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Colaboration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String royalties;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "colaboration_genres", joinColumns = @JoinColumn(name = "colaboration_id"))
    @Column(name = "genre")
    private List<Genre> genres;

    private boolean remote;

    private LocalDateTime deadline;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime createdAt = LocalDateTime.now();

    public String getTimeAgo() {
        if (createdAt == null) return "";
        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        long days = hours / 24;
        return days + " days ago";
    }
}
