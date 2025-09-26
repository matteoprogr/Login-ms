package com.ms.LoginAuthetication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Document("confirmation_keys")
@Data
public class ConfirmationKey {

    @Id
    private String id;
    private String key;
    private UUID userId;
    private String expiryDate;

    public boolean isExpired(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        LocalDateTime expiry = LocalDateTime.parse(expiryDate, formatter);
        return LocalDateTime.now().isAfter(expiry);
    }
}
