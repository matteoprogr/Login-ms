package com.ms.LoginAuthetication.service;

import com.ms.LoginAuthetication.model.ConfirmationKey;
import com.ms.LoginAuthetication.model.User;
import com.ms.LoginAuthetication.repository.ConfirmationKeyRepository;
import com.ms.LoginAuthetication.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ConfirmationKeyService {

    private final ConfirmationKeyRepository keyRepository;
    private final UserRepository userRepository;

    public ConfirmationKeyService(ConfirmationKeyRepository keyRepository, UserRepository userRepository) {
        this.keyRepository = keyRepository;
        this.userRepository = userRepository;
    }

    public String createKey(User user){
        String key = UUID.randomUUID().toString();
        ConfirmationKey confirmationKey = new ConfirmationKey();
        confirmationKey.setKey(key);
        confirmationKey.setUserId(user.getId());
        confirmationKey.setExpiryDate(expiryDate());
        keyRepository.save(confirmationKey);
        return key;
    }

    public boolean verifyKey(String key){
        return keyRepository.findByKey(key)
                .filter(k -> !k.isExpired())
                .map(k -> {
                    User user = userRepository.findById(k.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    user.setIsActive(true);
                    userRepository.save(user);
                    keyRepository.delete(k);
                    return true;
                }).orElse(false);
    }

    public String expiryDate(){
        LocalDateTime dateTime = LocalDateTime.now().plusHours(24);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return dateTime.format(formatter);
    }
}
