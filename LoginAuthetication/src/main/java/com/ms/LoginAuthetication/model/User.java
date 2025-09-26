package com.ms.LoginAuthetication.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document("users")
@Data
public class User {

    @Id
    private UUID id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<>();
    private Boolean isActive;
}
