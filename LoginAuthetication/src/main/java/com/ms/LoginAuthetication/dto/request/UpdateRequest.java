package com.ms.LoginAuthetication.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
public class UpdateRequest {

    private UUID id;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email(message = "Email non valida")
    private String email;

    @NotBlank
    @Size(min = 8, max = 100)
    private String password;

    private Set<String> roles = new HashSet<>();
    private Boolean isActive;

}
