package com.ms.LoginAuthetication.configuration;

import com.ms.LoginAuthetication.model.User;
import com.ms.LoginAuthetication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.ms.LoginAuthetication.model.ERole.ROLE_ADMIN;

@Configuration
public class AdminInitializer {

    @Value("${app.admin.username}")
    private String username;

    @Value("${app.admin.password}")
    private String password;

    @Value("${app.admin.email}")
    private String email;

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder encoder){
        return args -> {
            if(userRepository.count() == 0){
                String hashedPassword = encoder.encode(password);
                User admin = new User();
                admin.setId(UUID.randomUUID());
                admin.setUsername(username);
                admin.setEmail(email);
                admin.setPassword(hashedPassword);
                admin.setRoles(new HashSet<>(Set.of(ROLE_ADMIN.name())));
                userRepository.save(admin);
            }
        };
    }
}
