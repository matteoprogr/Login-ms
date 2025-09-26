package com.ms.LoginAuthetication.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private String username;
    private String email;
    private List<String> roles;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty
    private String tokenType;
}
