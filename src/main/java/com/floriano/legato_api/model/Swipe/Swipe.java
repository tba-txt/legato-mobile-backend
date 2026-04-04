package com.floriano.legato_api.model.Swipe;

import com.floriano.legato_api.model.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "swipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Swipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "swiper_id", nullable = false)
    private User swiper; // Quem deu o swipe

    @ManyToOne
    @JoinColumn(name = "swiped_id", nullable = false)
    private User swiped; // Quem recebeu o swipe

    @Column(name = "is_like", nullable = false)
    private boolean isLike; // true = Like, false = Dislike (Pass)

    private LocalDateTime createdAt = LocalDateTime.now();
}