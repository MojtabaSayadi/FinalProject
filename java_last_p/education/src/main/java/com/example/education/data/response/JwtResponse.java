package com.example.education.data.response;


import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String result;
    private Long id;

    public JwtResponse(String accessToken, String username, Collection<? extends GrantedAuthority> authorities) {
        this.token = accessToken;
        this.username = username;
        this.authorities = authorities;
    }

    public JwtResponse(String accessToken, String username, Collection<? extends GrantedAuthority> authorities, String result, Long id) {
        this.token = accessToken;
        this.username = username;
        this.authorities = authorities;
        this.result = result;
        this.id = id;
    }
}