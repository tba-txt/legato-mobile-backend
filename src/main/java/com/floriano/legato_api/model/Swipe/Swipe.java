package com.floriano.legato_api.model.Swipe;

import com.floriano.legato_api.model.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "swipes",
    // BLINDAGEM 1: O Banco de Dados nunca vai aceitar dois swipes da mesma pessoa para o mesmo alvo.
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"swiper_id", "swiped_id"})
    }
)
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
    private User swiper;

    @ManyToOne
    @JoinColumn(name = "swiped_id", nullable = false)
    private User swiped;

    @Column(name = "is_like", nullable = false)
    private boolean isLike;

    private LocalDateTime createdAt = LocalDateTime.now();
}