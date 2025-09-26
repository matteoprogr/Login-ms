package com.ms.LoginAuthetication.security.services;

import com.ms.LoginAuthetication.model.User;
import com.ms.LoginAuthetication.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Slf4j
public class UserDetailsServiceImpl  implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(mail).orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Recuperato utente con mail: {}" , mail);
        return UserDetailsImpl.build(user);
    }
}
