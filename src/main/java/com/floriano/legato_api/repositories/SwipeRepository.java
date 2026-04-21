package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Swipe.Swipe;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    
    // Verifica se A já avaliou B
    boolean existsBySwiperAndSwiped(User swiper, User swiped);

    // BLINDAGEM 2: Se por acaso existirem 2 likes (dados antigos), o 'exists' não quebra a aplicação, 
    // ele só responde TRUE de forma super rápida e performática.
    boolean existsBySwiperAndSwipedAndIsLikeTrue(User swiper, User swiped);

    // Mantemos esse caso você precise no futuro, mas o 'findFirst' garante que NUNCA 
    // dará o erro NonUniqueResultException se o banco estiver sujo.
    Optional<Swipe> findFirstBySwiperAndSwipedAndIsLikeTrue(User swiper, User swiped);

    @Query("SELECT s.swiped.id FROM Swipe s WHERE s.swiper.id = :swiperId")
    List<Long> findSwipedIdsBySwiperId(@Param("swiperId") Long swiperId);

    @Query("SELECT s.swiped.id FROM Swipe s WHERE s.swiper.id = :swiperId AND s.isLike = true")
    List<Long> findUsersWhoLikedMe(@Param("swiperId") Long swiperId);
}