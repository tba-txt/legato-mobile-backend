package com.floriano.legato_api.repositories;

import com.floriano.legato_api.model.Colaboration.Colaboration;
import com.floriano.legato_api.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColaborationRepository extends JpaRepository<Colaboration, Long> {

    List<Colaboration> findByUser(User user);

}
