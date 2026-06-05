package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Swipe.Swipe;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    
    boolean existsBySwiperAndSwiped(User swiper, User swiped);

    boolean existsBySwiperAndSwipedAndIsLikeTrue(User swiper, User swiped);

    Optional<Swipe> findFirstBySwiperAndSwipedAndIsLikeTrue(User swiper, User swiped);

    @Query("SELECT s.swiped.id FROM Swipe s WHERE s.swiper.id = :swiperId")
    List<Long> findSwipedIdsBySwiperId(@Param("swiperId") Long swiperId);

    @Query("SELECT s.swiped.id FROM Swipe s WHERE s.swiper.id = :swiperId AND s.isLike = true")
    List<Long> findUsersWhoLikedMe(@Param("swiperId") Long swiperId);

    List<Swipe> findBySwiperIdOrderByCreatedAtDesc(Long swiperId);
}