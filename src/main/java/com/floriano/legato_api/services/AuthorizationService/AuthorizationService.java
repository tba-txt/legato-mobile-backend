package com.floriano.legato_api.services.AuthorizationService;

import com.floriano.legato_api.model.User.UserPrincipal;
import com.floriano.legato_api.repositories.UserRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));

        if (!user.isActive()) {
            throw new DisabledException("Esta conta foi desativada.");
        }
        // ----------------------------

        return new UserPrincipal(user); 
    }
}
