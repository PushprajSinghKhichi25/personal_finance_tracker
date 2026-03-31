package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private Long id;
    private String username;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String tokenType;

    public AuthResponse(Long id, String username, String email, String accessToken) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }
}